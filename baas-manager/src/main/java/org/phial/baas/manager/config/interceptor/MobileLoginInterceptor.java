package org.phial.baas.manager.config.interceptor;

import org.mayanjun.myrest.interceptor.AnnotationBasedHandlerInterceptor;
import org.mayanjun.myrest.session.SessionUser;
import org.phial.baas.service.domain.entity.rbac.SysUser;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录检查拦截器
 *
 * @author mayanjun
 * @since 2019-10-10
 */
@Component
public class MobileLoginInterceptor extends AnnotationBasedHandlerInterceptor {

    private MobileSessionManager session;

    public MobileLoginInterceptor(MobileSessionManager session) {
        this.session = session;
    }

    @Override
    public int getOrder() {
        return InterceptorOrder.LOGIN.ordinal();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            SessionUser<SysUser> user = session.getUser(request);
            if (user == null) return false;
            session.updateLastLoginTime(user.getUsername());
            return true;
        } catch (Exception e) {
            session.clear();
            throw e;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        session.clear();
    }
}