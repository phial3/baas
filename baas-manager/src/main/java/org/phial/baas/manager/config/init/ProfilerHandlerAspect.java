package org.phial.baas.manager.config.init;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.mayanjun.mybatisx.dal.dao.BasicDAO;
import org.mayanjun.myrest.util.JSON;
import org.phial.baas.manager.config.interceptor.ConsoleSessionManager;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 日志记录器处理器
 * @since 2019-10-10
 * @author mayanjun
 */
@Order
@Aspect
@Component
public class ProfilerHandlerAspect extends CachedAspect<Profiler> {

    private ConsoleSessionManager sessionManager;

    private BasicDAO dao;

    private ThreadPoolTaskExecutor executor;

    private Object lock = new Object();

    public ProfilerHandlerAspect(ConsoleSessionManager sessionManager, BasicDAO dao, ThreadPoolTaskExecutor executor) {
        this.sessionManager = sessionManager;
        this.dao = dao;
        this.executor = executor;
    }

    @Pointcut("@annotation(org.phial.baas.manager.config.init.Profiler)")
    public void pointcut(){
    }


    @Around("pointcut()")
    public Object profiler(ProceedingJoinPoint jp) throws Throwable {

        return null;
    }

    private String serializeArguments(Object args[] ) {
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
        } else if (o instanceof ServletRequest){
            return "\"" + o.getClass().getCanonicalName() + "\"";
        } else if (o instanceof ServletResponse) {
            return "\"" + o.getClass().getCanonicalName() + "\"";
        } else {
            return JSON.se(o);
        }
    }

}
