package org.phial.baas.manager.handler;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author gyf
 * @date 2022/12/12
 */
@Slf4j
public class CustomHandlerExceptionResolver implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        ResponseEntity<Exception> resp = ResponseEntity.ok(ex);
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //Service exception,handler exception from service
            log.error("resolveException: handler:[{}:{}] content:{}",
                    handlerMethod.getBean().getClass().getName(),
                    handlerMethod.getMethod().getName(),
                    ex.getCause().getMessage(),
                    ex);
        } else {
            if (ex instanceof NoHandlerFoundException) {
                log.error("interface [" + request.getRequestURI() + "] not exist");
            } else {
                log.error(ex.getMessage(), ex);
            }
        }

        handlerResponse(response, resp);
        return new ModelAndView();

    }

    public static HttpServletResponse handlerResponse(HttpServletResponse response, ResponseEntity resp) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.setStatus(200);
        try {
            response.getWriter().write(JSONObject.toJSONString(resp));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return response;
    }
}