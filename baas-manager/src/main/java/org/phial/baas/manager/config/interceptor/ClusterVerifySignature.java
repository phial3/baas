package org.phial.baas.manager.config.interceptor;

import org.mayanjun.myrest.interceptor.Interceptor;

import java.lang.annotation.*;

/**
 * 集群接口调用的参数校验
 * @since 2021/1/15
 * @author mayanjun
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Interceptor(loadFromContainer = true, value = ClusterVerifySignatureInterceptor.class)
public @interface ClusterVerifySignature {
}