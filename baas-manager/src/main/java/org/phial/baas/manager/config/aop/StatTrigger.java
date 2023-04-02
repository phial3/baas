package org.phial.baas.manager.config.aop;


import java.lang.annotation.*;

/**
 * 用以触发统计事件事件
 * @since 2021/4/8
 * @author mayanjun
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface StatTrigger {

    /**
     * 返回触发的统计类型
     * @return
     */
    StateEvent.StatType[] value();
}