package org.phial.baas.manager.config.source;//package org.phial.baas.config.source;
//
//import com.zaxxer.hikari.HikariDataSource;
//import lombok.extern.slf4j.Slf4j;
//import org.phial.baas.constant.CommonConstant;
//import org.phial.baas.factory.BaasApplicationContext;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
//import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
//import org.springframework.stereotype.Component;
//
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author gyf
// * @date 2022/12/12
// *
// * @Dependson 注解与 @ConditionalOnBean 注解的区别:
// * @Dependson: 注解是在另外一个实例创建之后才创建当前实例，也就是，最终两个实例都会创建，只是顺序不一样
// * @ConditionalOnBean: 注解是只有当另外一个实例存在时，才创建，否则不创建，也就是，最终有可能两个实例都创建了，有可能只创建了一个实例，也有可能一个实例都没创建
// */
//@Slf4j
//@Component
//@ConditionalOnBean({DataSource.class})
//public class DynamicRoutingDataSource extends AbstractRoutingDataSource {
//
//    private final HashMap<String, DataSource> dataSourceMap = new HashMap<>();
//
//    public DynamicRoutingDataSource() {
//
//        DataSource masterDataSource = BaasApplicationContext.getBean("master", HikariDataSource.class);
//        DataSource slaveDataSource = BaasApplicationContext.getBean("slave", HikariDataSource.class);
//        dataSourceMap.put("master", masterDataSource);
//        dataSourceMap.put("slave", salveDataSource);
//        log.info("DynamicRoutingDataSource construct finished dataSourceMap keys:{}", dataSourceMap.keySet());
//
//        this.setTargetDataSources(new HashMap<>(dataSourceMap));
//        this.setDefaultTargetDataSource(dataSourceMap.get(CommonConstant.DEFAULT_SOURCE));
//    }
//
//    @Override
//    protected DataSource determineTargetDataSource() {
//        String source = DynamicDataSourceContextHolder.getCurrentSource();
//        log.info("DynamicRoutingDataSource determineTargetDataSource() source:{} dataSourceMapSize:{}", source, dataSourceMap.size());
//        return dataSourceMap.get(source);
//    }
//
//    @Override
//    protected Object determineCurrentLookupKey() {
//        String source = DynamicDataSourceContextHolder.getCurrentSource();
//        log.debug("DynamicRoutingDataSource determineCurrentLookupKey() current source:{}", source);
//        return source;
//    }
//
//    public Map<String, DataSource> getDataSourceMap() {
//        return dataSourceMap;
//    }
//}
