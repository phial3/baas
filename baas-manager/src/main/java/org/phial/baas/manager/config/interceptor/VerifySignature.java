package org.phial.baas.manager.config.interceptor;

import org.mayanjun.myrest.interceptor.Interceptor;

import java.lang.annotation.*;

/**
 * 校验参数
 * @since 2019-10-10
 * @author mayanjun
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Interceptor(loadFromContainer = true, value = VerifySignatureInterceptor.class)
public @interface VerifySignature {
}