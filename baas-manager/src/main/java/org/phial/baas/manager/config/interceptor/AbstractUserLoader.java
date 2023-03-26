package org.phial.baas.manager.config.interceptor;

import org.mayanjun.myrest.session.UserLoader;
import org.phial.baas.service.domain.entity.sys.AbstractUser;

public abstract class AbstractUserLoader<T extends AbstractUser> implements UserLoader<T> {

    private ConfigurableSession<T> session;

    public AbstractUserLoader(ConfigurableSession<T> session) {
        this.session = session;
    }

    public ConfigurableSession<T> session() {
        return session;
    }
}
