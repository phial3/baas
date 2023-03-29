package org.phial.baas.manager.config.mvc;

import lombok.extern.slf4j.Slf4j;
import org.mayanjun.mybatisx.api.annotation.Column;
import org.mayanjun.myrest.RestResponse;
import org.mayanjun.myrest.interceptor.ApplicationExceptionHandler;
import org.phial.baas.manager.config.app.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.lang.reflect.Field;

/**
 * Servlet初始化回调
 * @since 2019-10-10
 * @author mayanjun
 * @vendor mayanjun.org
 * @generator consolegen 1.0
 * @manufacturer https://mayanjun.org
 */
@Slf4j
@Component
public class ServletContextConfig implements ServletContextInitializer {

    @Autowired
    private AppConfig config;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        log.info("ServletContextConfig onStartup() ...");
        ApplicationExceptionHandler.installUnknownExceptionHandler(t -> {
            RestResponse response = RestResponse.error();
            FieldError error = ((BindException) t).getFieldError();
            String fieldName = error.getField();
            Object target = ((BindException) t).getTarget();
            try {
                Field field = target.getClass().getDeclaredField(fieldName);
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    String comment = column.comment();
                    response.setMessage(comment + " 参数错误").add("description", t.getMessage());
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            return response;
        });
    }
}
