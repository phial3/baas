package org.phial.baas.manager.config.interceptor;

import org.mayanjun.myrest.session.UserLoader;
import org.mayanjun.util.BouncyCastleCrypto;

import org.phial.baas.manager.config.app.AppConfig;
import org.phial.baas.manager.config.cache.CacheClient;
import org.phial.baas.service.domain.entity.rbac.SysUser;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 会话管理器，管理登录状态
 * @since 2019-07-06
 * @author mayanjun
 */
@Component
public class ConsoleSessionManager extends ConfigurableSession<SysUser> implements InitializingBean {

    @Autowired
    private CacheClient cacheClient;

    @Override
    protected void configSession(UserLoader<SysUser> userLoader) {
        setUserLoader(userLoader);
        AppConfig config = config();
        setDomain(config.getDomain());
        setTokenName(config.getTokenCookieName());
        setCrypto(new BouncyCastleCrypto(config.getConsoleAesKey().secretKeyStore()));
    }

    @Override
    protected UserLoader<SysUser> createUserLoader() {
        if (config().isClusterEnabled()) {
            return new RedisConsoleUserLoader(this, cacheClient);
        }
        return new ConsoleUserLoader(this);
    }
}