package org.phial.baas.service.domain.entity.sys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 修饰一个属性项
 * @since 2020/11/25
 * @author mayanjun
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AttributeItem {

    /**
     * 所属用户，空字符串表示系统设置，*表示属于用户个性化设置
     * @return
     */
    String user() default "";

    /**
     * 属性的所属组
     * @return
     */
    String group() default "";

}
