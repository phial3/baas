package org.phial.baas.manager.config.init;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HeadInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String scheme = request.getHeader("request-scheme");
        if (scheme == null) {
            scheme = request.getScheme();
        }
        request.setAttribute("REQUEST_SCHEME", scheme);
        return true;
    }
}
