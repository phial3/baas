package org.phial.baas.manager.config.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.mayanjun.myrest.interceptor.AnnotationBasedHandlerInterceptor;
import org.mayanjun.myrest.session.SessionUser;
import org.phial.baas.service.domain.entity.rbac.SysUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录检查拦截器
 * @since 2019-10-10
 * @author mayanjun
 */
@Component
public class LoginInterceptor extends AnnotationBasedHandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(LoginInterceptor.class);

    @Autowired
    private ConsoleSessionManager session;

    @Override
    public int getOrder() {
        return InterceptorOrder.LOGIN.ordinal();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Login login = findAnnotation(Login.class, handler);

        if(login.checkUser()) {
            try {
                SessionUser<SysUser> user = session.getUser(request);
                if (user == null) return false;
                if (StringUtils.isNotBlank(user.getDescription())) {
                    request.setAttribute("__current_user", user.getUsername() + "(" + user.getDescription() + ")");
                } else {
                    request.setAttribute("__current_user", user.getUsername());
                }

                session.updateLastLoginTime(user.getUsername());

                return true;
            } catch (Exception e) {
                session.clear();
                LOG.error("Unknown exception on login, uri={}, message={}", request.getRequestURI(),  e.getMessage());
                String uri = request.getRequestURI();
                if (uri.startsWith("/api")) throw e;

                String loginURL = "/login";
                response.sendRedirect(loginURL);
                return false;
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        session.clear();
    }
}