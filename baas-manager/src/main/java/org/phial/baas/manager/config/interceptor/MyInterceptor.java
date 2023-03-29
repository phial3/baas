package org.phial.baas.manager.config.interceptor;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class MyInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("MyInterceptor preHandle() hendler:{}, url:{}, params:{}", handler.getClass().getName(), request.getRequestURL(), JSONUtil.toJsonStr(request.getParameterMap()));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("MyInterceptor postHandle() url:{}, params:{}, resp:{}", request.getRequestURL(), JSONUtil.toJsonStr(request.getParameterMap()), response.getStatus());

    }
}
