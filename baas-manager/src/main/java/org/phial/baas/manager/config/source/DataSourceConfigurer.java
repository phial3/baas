package org.phial.baas.manager.config.source;//package org.phial.baas.config.source;
//
//import lombok.extern.slf4j.Slf4j;
//import org.phial.baas.constant.CommonConstant;
//import org.phial.baas.factory.BaasApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import org.springframework.transaction.PlatformTransactionManager;
//
//import javax.sql.DataSource;
//import java.util.Map;
//
//@Slf4j
//@Configuration
//public class DataSourceConfigurer {
//
//    @Bean("dynamicDataSource")
//    public DataSource dynamicDataSource() {
//        DynamicRoutingDataSource routingDataSource = BaasApplicationContext.getBean(DynamicRoutingDataSource.class);
//        Map<String, DataSource> dataSourceMap = routingDataSource.getDataSourceMap();
//        DataSource dataSource = dataSourceMap.get(CommonConstant.DEFAULT_SOURCE);
//        return dataSource;
//    }
//
//    @Bean
//    public PlatformTransactionManager transactionManager() {
//        return new DataSourceTransactionManager(dynamicDataSource());
//    }
//}
