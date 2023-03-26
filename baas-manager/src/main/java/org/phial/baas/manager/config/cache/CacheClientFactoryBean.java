package org.phial.baas.manager.config.cache;

import org.phial.baas.manager.config.app.AppConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
public class CacheClientFactoryBean implements FactoryBean<CacheClient>, DisposableBean {

    private AppConfig config;

    private CacheClient client;

    public CacheClientFactoryBean(AppConfig config) {
        this.config = config;
    }

    @Override
    public CacheClient getObject() throws Exception {
        if (config.isClusterEnabled()) {
            client = new RedisCacheClient(config.getRedisHost(), config.getRedisPort());
        } else {
            if (config.isRedisEnabled()) {
                client = new RedisCacheClient(config.getRedisHost(), config.getRedisPort());
            }
        }
        return client;
    }

    @Override
    public Class<?> getObjectType() {
        return RedisCacheClient.class;
    }

    @Override
    public void destroy() throws Exception {
        client.destroy();
    }
}
