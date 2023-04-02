package org.phial.baas.manager.config.aop;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.mayanjun.mybatisx.api.query.Query;
import org.mayanjun.mybatisx.api.query.QueryBuilder;
import org.mayanjun.mybatisx.dal.dao.BasicDAO;
import org.mayanjun.myrest.session.SessionUser;
import org.mayanjun.myrest.util.JSON;
import org.phial.baas.manager.config.init.CachedAspect;
import org.phial.baas.manager.config.init.Profiler;
import org.phial.baas.manager.config.interceptor.ConsoleSessionManager;
import org.phial.baas.manager.util.NetUtils;
import org.phial.baas.service.domain.entity.rbac.SysUser;
import org.phial.baas.service.domain.entity.system.AccessLog;
import org.phial.baas.service.domain.entity.system.MethodMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 日志记录器处理器
 *
 * @author mayanjun
 * @since 2019-10-10
 */
@Order
@Aspect
@Component
public class ProfilerHandlerAspect extends CachedAspect<Profiler> {
    private static final Logger LOG = LoggerFactory.getLogger(ProfilerHandlerAspect.class);

    private ConsoleSessionManager sessionManager;

    private BasicDAO dao;

    private ThreadPoolTaskExecutor executor;

    private Object lock = new Object();

    private Map<Method, MethodMapping> methodCache = new ConcurrentHashMap<>();

    public ProfilerHandlerAspect(ConsoleSessionManager sessionManager, BasicDAO dao, ThreadPoolTaskExecutor executor) {
        this.sessionManager = sessionManager;
        this.dao = dao;
        this.executor = executor;
    }

    @Pointcut("@annotation(org.phial.baas.manager.config.init.Profiler)")
    public void pointcut() {
    }


    private MethodMapping getOrSaveMapping(Method method) {
        return methodCache.computeIfAbsent(method, m -> {
            String methodName = m.toString();

            Query<MethodMapping> query = QueryBuilder.custom(MethodMapping.class)
                    .andEquivalent("name", methodName)
                    .build();
            MethodMapping mapping = dao.queryOne(query);
            if (mapping == null) {

                synchronized (lock) {
                    mapping = dao.queryOne(query);
                    if (mapping == null) {

                        mapping = new MethodMapping();
                        mapping.setName(m.toString());
                        mapping.setClassName(method.getDeclaringClass().getCanonicalName());

                        try {
                            dao.save(mapping);
                        } catch (Exception e) {
                            LOG.warn("Can't save method: method=" + methodName, e);
                        }
                    }
                }
            }
            return mapping;
        });
    }


    @Around("pointcut()")
    public Object profiler(ProceedingJoinPoint jp) throws Throwable {
        MethodSignature msig = (MethodSignature) jp.getSignature();
        Method method = msig.getMethod();
        Profiler profiler = annotation(method);
        if (profiler.ignore()) return jp.proceed();

        long now = System.currentTimeMillis();
        AccessLog log = new AccessLog();
        log.setDate(new Date());
        log.setException(false);

        log.setProfilerName(profiler.value());
        log.setServerAddress(NetUtils.guessServerIp());
        MethodMapping methodMapping = getOrSaveMapping(method);

        log.setMethodId(methodMapping.getId());

        RequestAttributes ra = RequestContextHolder.currentRequestAttributes();
        if (ra instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) ra).getRequest();
            if (request != null) {
                log.setContentType(request.getContentType());
                log.setHttpMethod(request.getMethod());
                log.setUri(request.getRequestURI());
                log.setUserAgent(request.getHeader("User-Agent"));

                String remoteAddr = request.getRemoteAddr();
                if (NetUtils.isLocal(remoteAddr)) {
                    remoteAddr = request.getHeader("X-Forwarded-For");
                }
                log.setClientAddress(remoteAddr);
            }
        }

        try {
            SessionUser<SysUser> user = sessionManager.getCurrentUser();
            log.setUser(user.getUsername());
        } catch (Exception e) {
            //LOG.error("ProfilerHandlerAspect profiler getCurrentUser error!", e);
        }

        if (profiler.serializeArguments()) {
            Object[] args = jp.getArgs();
            String json = serializeArguments(args);
            if (StringUtils.isBlank(json)) {
                json = "";
            }
            log.setParameters(json);
        } else {
            log.setParameters("ARGUMENTS IGNORED");
        }

        try {
            Object returnValue = jp.proceed();
            return returnValue;
        } catch (Throwable e) {
            log.setException(true);
            String m = e.getMessage();
            if (m != null && m.length() > 200) {
                m = m.substring(0, 200);
            }
            log.setMessage(m);
            throw e;
        } finally {
            try {
                if (!log.getException()) {
                    log.setMessage("SUCCESS");
                }
                log.setElapsed(System.currentTimeMillis() - now);
                // async save log
                executor.submit(() -> dao.save(log));
            } catch (Exception e) {
                LOG.error("Save access log error", e);
            }
        }
    }

    private String serializeArguments(Object args[]) {
        if (args == null) return "";
        if (args.length == 0) return "[]";

        StringBuffer sb = new StringBuffer("[");

        for (int i = 0; i < args.length; i++) {
            sb.append(serialize(args[i]));
            if (i < args.length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String serialize(Object o) {
        if (o instanceof MultipartFile) {
            MultipartFile mf = (MultipartFile) o;
            return "\"" + mf.getContentType() + ":" + mf.getSize() + ":" + mf.getName() + ":" + mf.getOriginalFilename() + "\"";
        } else if (o instanceof ServletRequest) {
            return "\"" + o.getClass().getCanonicalName() + "\"";
        } else if (o instanceof ServletResponse) {
            return "\"" + o.getClass().getCanonicalName() + "\"";
        } else {
            return JSON.se(o);
        }
    }

}
