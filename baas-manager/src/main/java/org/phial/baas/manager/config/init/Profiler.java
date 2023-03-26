package org.phial.baas.manager.config.init;

import java.lang.annotation.*;

/**
 * 日志分析工具注解
 * @since 2019-10-10
 * @author mayanjun
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface Profiler {

    /**
     * 业务名称
     * @return
     */
    String value() default "";

    /**
     * 是否忽略记录
     * @return
     */
    boolean ignore() default false;

    /**
     *
     * @return
     */
    boolean serializeArguments() default true;
}