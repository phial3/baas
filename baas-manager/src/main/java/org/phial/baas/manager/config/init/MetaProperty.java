package org.phial.baas.manager.config.init;

/**
 * 元属性，可配合注解进行属性配置
 * @since 2019-10-10
 * @author mayanjun
 */
public @interface MetaProperty {

    String name();

    String value();
}
