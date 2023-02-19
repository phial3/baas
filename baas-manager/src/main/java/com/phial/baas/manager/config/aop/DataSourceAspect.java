package com.phial.baas.manager.config.aop;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gyf
 * @date 2022/12/12
 */
//@Aspect
//@Component
@Slf4j
public class DataSourceAspect {

    @Pointcut("execution(* com.phial.baas.manager..*(..))")
    public void doAspect() {
    }

    @Around("doAspect()")
    public void aroundDataSource(JoinPoint point) {
        log.info("DataSourceAspect aroundDataSource() Around point:{}", JSONUtil.toJsonStr(point));
    }

    /**
     * Switch DataSource
     */
    @Before("doAspect()")
    public void switchDataSource(JoinPoint point) {
        log.info("DataSourceAspect switchDataSource() Before point:{}", JSONUtil.toJsonStr(point));
    }

    /**
     * @param point the point
     */
    @After("doAspect()")
    public void restoreDataSource(JoinPoint point) {
        log.info("DataSourceAspect restoreDataSource() After point:{}", JSONUtil.toJsonStr(point));
    }
}
