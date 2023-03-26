package org.phial.baas.manager.config.interceptor;

import com.fasterxml.jackson.core.type.TypeReference;
import org.mayanjun.myrest.session.SessionUser;
import org.phial.baas.manager.config.cache.CacheClient;
import org.phial.baas.manager.config.cache.CacheKey;
import org.phial.baas.service.domain.entity.rbac.SysUser;

/**
 * 用户加载器的 Redis 实现
 * @since 2020/12/9
 * @author mayanjun
 */
public class RedisConsoleUserLoader extends ConsoleUserLoader {

    private CacheClient client;

    private TypeReference<SessionUser<SysUser>> typeReference;

    public RedisConsoleUserLoader(ConfigurableSession<SysUser> session, CacheClient cacheClient) {
        super(session);
        this.typeReference = new TypeReference<SessionUser<SysUser>>() {};
        this.client = cacheClient;
    }


    @Override
    public void setUserCache(SessionUser<SysUser> user) {
        client.set(CacheKey.CONSOLE_USER, user.getUsername(), user);
    }

    @Override
    public void removeUserCache(SessionUser<SysUser> user) {
        client.delete(CacheKey.CONSOLE_USER, user.getUsername());
    }

    @Override
    public SessionUser<SysUser> getUserFromCache(String username) {
        return client.get(CacheKey.CONSOLE_USER, username, typeReference);
    }
}
