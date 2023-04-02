package org.phial.baas.manager.config.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.mayanjun.myrest.util.JSON;
import org.phial.baas.manager.config.init.CachedAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 事件触发器切面
 * @since 2021/4/8
 * @author mayanjun
 */
@Order
@Aspect
@Component
public class StatTriggerHandlerAspect extends CachedAspect<StatTrigger> {

    private static final Logger LOG = LoggerFactory.getLogger(StatTriggerHandlerAspect.class);


    @Pointcut("@annotation(org.phial.baas.manager.config.aop.StatTrigger)")
    public void pointcut(){
    }

    @Around("pointcut()")
    public Object triggerStatEvent(ProceedingJoinPoint jp) throws Throwable {
        Object returnValue = jp.proceed();

        // 正常返回之前触发事件
        MethodSignature msig = (MethodSignature) jp.getSignature();
        Method method = msig.getMethod();
        StatTrigger trigger = annotation(method);
        StateEvent.StatType[] types = trigger.value();
        if (types != null && types.length > 0) {
            StateEvent[] events = new StateEvent[types.length];
            for (int i = 0; i < types.length; i++) {
                events[i] = new StateEvent(types[i]);
            }
            LOG.info("Aspect stat event published: method={}, events={}", method.toString(), JSON.se(events));
        }

        return returnValue;
    }

}
