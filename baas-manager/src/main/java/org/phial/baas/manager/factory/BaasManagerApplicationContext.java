package org.phial.baas.manager.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author gyf
 * @date 2022/12/12
 */
@Slf4j
@Component
public class BaasManagerApplicationContext implements ApplicationContextAware, BeanPostProcessor {

    private static ApplicationContext ctx;

    @PostConstruct
    public void init() {
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return ctx;
    }

    public static <T> T getBean(Class<T> beanClass) {
        return ctx.getBean(beanClass);
    }

    public static <T> T getBean(String beanName, Class<T> beanClass) {
        return ctx.getBean(beanName, beanClass);
    }

    public static Object getBean(String beanName) {
        return ctx.getBean(beanName);
    }

}
