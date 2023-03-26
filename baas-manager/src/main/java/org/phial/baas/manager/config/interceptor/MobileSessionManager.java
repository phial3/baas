package org.phial.baas.manager.config.interceptor;

import org.mayanjun.core.Assert;
import org.mayanjun.core.ServiceException;
import org.mayanjun.core.Status;
import org.mayanjun.myrest.session.SessionUser;
import org.mayanjun.myrest.session.UserLoader;
import org.mayanjun.util.BouncyCastleCrypto;
import org.phial.baas.manager.config.app.AppConfig;
import org.phial.baas.manager.config.cache.CacheClient;
import org.phial.baas.service.domain.entity.rbac.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 移动会话管理器，管理登录状态
 * @since 2019-07-06
 * @author mayanjun
 */
@Component
public class MobileSessionManager extends ConfigurableSession<SysUser> {

    public static Status TOKEN_EXPIRED = new Status(2004, "会话已过期");

    private ThreadLocal<String> localRequestToken = new ThreadLocal<>();

    @Autowired
    private CacheClient cacheClient;

    @Override
    protected void configSession(UserLoader<SysUser> userLoader) {
        setUserLoader(userLoader);
        AppConfig config = config();
        Assert.notBlank(config.getMobileDomain(), "请配置移动登录域名：app-config.mobile-domain");
        setDomain(config.getMobileDomain());
        setTokenName(config.getMobileTokenCookieName());
        setCrypto(new BouncyCastleCrypto(config.getMobileAesKey().secretKeyStore()));
    }

    @Override
    protected UserLoader<SysUser> createUserLoader() {
        if (config().isClusterEnabled()) {
            return new RedisMobileUserLoader(this, cacheClient);
        }
        return new MobileUserLoader(this);
    }

    /**
     * 系统内退出登录
     * @param username
     */
    public void signOut(String username) {
        getUserLoader().removeUserCache(new SessionUser<>(username));
    }

    @Override
    public SessionUser<SysUser> signIn(String username, String password, HttpServletResponse response) {

        Assert.notBlank(username, USERNAME_OR_PASSWORD_INCORRECT);
        Assert.notBlank(password, USERNAME_OR_PASSWORD_INCORRECT);

        UserLoader<SysUser> userLoader = getUserLoader();
        SessionUser<SysUser> user = userLoader.loadUser(username);
        Assert.notNull(user, USERNAME_OR_PASSWORD_INCORRECT);
        String dbPassword = decryptPassword(user.getPassword());
        Assert.isTrue(password.equals(dbPassword), USERNAME_OR_PASSWORD_INCORRECT);

        SessionUser<SysUser> loginUser = setUser(user);
        userLoader.setUserCache(loginUser);
        response.addCookie(createSigninCookie(loginUser.getToken()));
        return loginUser;
    }

    public SessionUser<SysUser> setUser(SessionUser<SysUser> user) {
        SessionUser<SysUser> loginUser = new SessionUser(user);
        loginUser.setOriginUser(user.getOriginUser());
        String cookiePlain = user.getUsername() + ";" + loginUser.getLastLoginTime();
        String token = encryptToken(cookiePlain);

        // BUG TO BE FIXED
        loginUser.setToken(token);
        loginUser.getOriginUser().setToken(token);
        localRequestToken.set(token);
        return loginUser;
    }

    public SessionUser<SysUser> signInByIdCard(String username, HttpServletResponse response) {

        Assert.notBlank(username, USERNAME_OR_PASSWORD_INCORRECT);

        UserLoader<SysUser> userLoader = getUserLoader();
        SessionUser<SysUser> user = userLoader.loadUser(username);
        Assert.notNull(user, USERNAME_OR_PASSWORD_INCORRECT);
        SessionUser<SysUser> loginUser = new SessionUser(user);
        loginUser.setOriginUser(user.getOriginUser());
        String cookiePlain = user.getUsername() + ";" + loginUser.getLastLoginTime();
        String token = encryptToken(cookiePlain);

        // BUG TO BE FIXED
        loginUser.setToken(token);
        loginUser.getOriginUser().setToken(token);

        userLoader.setUserCache(loginUser);
        response.addCookie(createSigninCookie(token));
        return loginUser;
    }

    @Override
    protected String getToken(HttpServletRequest request) throws ServiceException {
        String token = super.getToken(request);
        localRequestToken.set(token);
        return token;
    }

    @Override
    public SessionUser<SysUser> getUser(HttpServletRequest request) {
        SessionUser<SysUser> user = super.getUser(request);
        Assert.isTrue(localRequestToken.get().equals(user.getToken()), TOKEN_EXPIRED);
        localRequestToken.remove();
        return user;
    }
}