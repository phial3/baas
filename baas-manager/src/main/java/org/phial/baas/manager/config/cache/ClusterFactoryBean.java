package org.phial.baas.manager.config.cache;

import org.mayanjun.mybatisx.dal.util.ClassUtils;
import org.phial.baas.manager.config.app.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartFactoryBean;

public abstract class ClusterFactoryBean<T extends DisposableBean> implements SmartFactoryBean<T>, DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(ClusterFactoryBean.class);

    private AppConfig config;

    private T object;

    public ClusterFactoryBean(AppConfig config) {
        this.config = config;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public T getObject() throws Exception {
        T bean = null;
        if(config.isClusterEnabled()) {
            bean = createClusterObject(config);
        } else {
            bean = createLocalObject(config);
        }

        this.object = bean;

        LOG.info("Cluster spring object created: bean={}", bean);

        if (this.object != null && this.object instanceof InitializingBean) {
            ((InitializingBean) this.object).afterPropertiesSet();
        }

        return bean;
    }

    protected abstract T createClusterObject(AppConfig config);

    protected abstract T createLocalObject(AppConfig config);

    @Override
    public Class<?> getObjectType() {
        return ClassUtils.getFirstParameterizedType(this.getClass());
    }

    @Override
    public void destroy() throws Exception {
        if (this.object != null) {
            this.object.destroy();
        }
    }

    @Override
    public boolean isEagerInit() {
        return true;
    }
}
