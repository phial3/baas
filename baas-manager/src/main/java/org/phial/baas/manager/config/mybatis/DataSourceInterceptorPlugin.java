package org.phial.baas.manager.config.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Properties;

/**
 * @author gyf
 * @date 2022/12/12
 */
@Slf4j
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class})})
public class DataSourceInterceptorPlugin implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 1.先看有没有注解，有注解就使用该注解对应的datasource
        Object[] orginArgs = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) orginArgs[0];
        String className = mappedStatement.getId().substring(0, mappedStatement.getId().lastIndexOf("."));
//        DataSource dataSource = Class.forName(className, true, Thread.currentThread().getContextClassLoader()).getAnnotation(DataSource.class);
//        if (dataSource == null) {
//            // 2.没有注解，按照source动态切换
//            String source = DynamicDataSourceContextHolder.getCurrentSource();
//            String dataSourceName = DynamicDataSourceContextHolder.reloadCurrentDataSource(source);
//            logger.info("DataSourceInterceptor plugin intercept source:{}, dataSourceName:{}", source, dataSourceName);
//        } else {
//            String dataSourceAnotationValue = dataSource.value();
//            if(MultiDataSources.containsDataSourceType(dataSourceAnotationValue)) {
//                MultiDataSourcesSwitcher.setDataSourceType(dataSourceAnotationValue);
//            } else {
//                multiDataSources.addDataSource(dataSourceAnotationValue);
//                if (MultiDataSources.containsDataSourceType(dataSourceAnotationValue)){
//                    MultiDataSourcesSwitcher.setDataSourceType(dataSourceAnotationValue);
//                }else {
//                    throw new IllegalArgumentException(String.format("invalid dbtype %s found on %s", dataSourceAnotationValue, className));
//                }
//            }
//        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }
}
