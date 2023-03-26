package org.phial.baas.manager.config.init;

import org.phial.baas.manager.util.CommonUtils;

import java.lang.annotation.*;
import java.lang.reflect.Method;

/**
 * 权限注解
 * @since 2019-10-10
 * @author mayanjun
 * @vendor mayanjun.org
 * @generator consolegen 1.0
 * @manufacturer https://mayanjun.org
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface Privileged {

    /**
     * 权限名称/描述
     * @return
     */
    String value();

    /**
     * 依赖的权限。有时候分配一个权限必须连带分配别的权限，这些权限称为依赖。
     * 这里的值是 packageName.ClassName::methodName.
     * dependencies 里面可以使用 {thisClass} 来引用当前类的全限定名
     * @see CommonUtils#getReferenceMethodName(Class, Method)
     * @return
     */
    Dependency[] dependencies() default {};
}