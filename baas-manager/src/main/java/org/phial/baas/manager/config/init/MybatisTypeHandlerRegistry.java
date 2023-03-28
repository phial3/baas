package org.phial.baas.manager.config.init;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mayanjun.mybatisx.dal.dao.BasicDAO;
import org.mayanjun.mybatisx.dal.dao.DatabaseSession;
import org.phial.baas.service.annootation.IEnum;
import org.phial.baas.manager.config.mybatis.MybatisEnumTypeHandler;
import org.phial.baas.service.constant.CommonConstant;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class MybatisTypeHandlerRegistry implements InitializingBean {
    private static final ResourcePatternResolver RESOURCE_PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory METADATA_READER_FACTORY = new CachingMetadataReaderFactory();

    @Autowired
    private BasicDAO dao;

    public void init() throws IOException {
        // 注册Enum类型处理类
        for (DatabaseSession databaseSession : dao.databaseRouter().getDatabaseSessions()) {
            Configuration configuration = databaseSession.sqlSession().getConfiguration();
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            Set<Class<?>> classes = scanClasses(CommonConstant.class.getPackage().getName(), IEnum.class);
            classes.stream()
                    .filter(Class::isEnum)
                    .filter(MybatisEnumTypeHandler::isMpEnums)
                    .forEach(cls -> {
                        typeHandlerRegistry.register(cls, MybatisEnumTypeHandler.class);
                        log.info("class:{} register EnumTypeHandler.", cls.getName());
                    });
        }
    }

    private Set<Class<?>> scanClasses(String packagePatterns, Class<?> assignableType) throws IOException {
        Set<Class<?>> classes = new HashSet<>();
        String[] packagePatternArray = StringUtils.tokenizeToStringArray(packagePatterns,
                ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
        for (String packagePattern : packagePatternArray) {
            Resource[] resources = RESOURCE_PATTERN_RESOLVER.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + ClassUtils.convertClassNameToResourcePath(packagePattern) + "/**/*.class");
            for (Resource resource : resources) {
                try {
                    ClassMetadata classMetadata = METADATA_READER_FACTORY.getMetadataReader(resource).getClassMetadata();
                    Class<?> clazz = Resources.classForName(classMetadata.getClassName());
                    if (assignableType == null || assignableType.isAssignableFrom(clazz)) {
                        classes.add(clazz);
                    }
                } catch (Throwable e) {
                    log.warn("Cannot load the '" + resource + "'. Cause by " + e.toString());
                }
            }
        }
        return classes;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }
}
