package org.phial.baas.manager.config.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class AuthRequiredInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {

        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 拦截可配置的链和合约，拦截指定URL
        HandlerMethod method = (HandlerMethod) handler;
        AuthRequired annotation = method.getMethodAnnotation(AuthRequired.class);
        if (null != annotation && req.getRequestURI().startsWith("/")) {

            String authStr = req.getHeader("auth");
            // header 解析,
            if (StringUtils.isBlank(authStr)) {
                log.error("AuthRequiredInterceptor failed, authStr={}", authStr);
                return false;
            }

            // 校验失败直接返回
            return false;
        }

        // 不需要拦截的方法返回true通过，返回false拦截
        return true;
    }
}
