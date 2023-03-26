package org.phial.baas.manager.config.interceptor;

import com.fasterxml.jackson.core.type.TypeReference;
import org.mayanjun.myrest.session.SessionUser;
import org.phial.baas.manager.config.cache.CacheClient;
import org.phial.baas.manager.config.cache.CacheKey;
import org.phial.baas.service.domain.entity.rbac.SysUser;

/**
 * 移动用户加载器
 * @since 2020/12/9
 * @author mayanjun
 */
public class RedisMobileUserLoader extends MobileUserLoader {

    private CacheClient client;

    private TypeReference<SessionUser<SysUser>> typeReference;

    public RedisMobileUserLoader(ConfigurableSession<SysUser> session, CacheClient cacheClient) {
        super(session);
        this.typeReference = new TypeReference<SessionUser<SysUser>>() {};
        this.client = cacheClient;
    }

    @Override
    public void setUserCache(SessionUser<SysUser> user) {
        client.set(CacheKey.MOBILE_USER, user.getUsername(), user);
    }

    @Override
    public void removeUserCache(SessionUser<SysUser> user) {
        client.delete(CacheKey.MOBILE_USER, user.getUsername());
    }

    @Override
    public SessionUser<SysUser> getUserFromCache(String username) {
        return client.get(CacheKey.MOBILE_USER, username, typeReference);
    }
}
