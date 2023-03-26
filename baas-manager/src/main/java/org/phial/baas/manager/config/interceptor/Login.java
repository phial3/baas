package org.phial.baas.manager.config.interceptor;

import org.mayanjun.myrest.interceptor.Interceptor;

import java.lang.annotation.*;

/**
 * 登录检查注解
 * @since 2019-10-10
 * @author mayanjun
 * @vendor mayanjun.org
 * @generator consolegen 1.0
 * @manufacturer https://mayanjun.org
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Interceptor(loadFromContainer = true, value = LoginInterceptor.class)
public @interface Login {

    boolean checkUser() default true;
}