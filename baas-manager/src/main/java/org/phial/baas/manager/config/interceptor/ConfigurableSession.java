package org.phial.baas.manager.config.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.mayanjun.core.Assert;
import org.mayanjun.core.ServiceException;
import org.mayanjun.mybatisx.dal.dao.BasicDAO;
import org.mayanjun.mybatisx.dal.util.ClassUtils;
import org.mayanjun.myrest.session.AESSession;
import org.mayanjun.myrest.session.SessionUser;
import org.mayanjun.myrest.session.UserLoader;
import org.phial.baas.manager.config.app.AppConfig;
import org.phial.baas.service.domain.entity.sys.AbstractUser;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;

@Slf4j
public abstract class ConfigurableSession<T extends AbstractUser> extends AESSession<T> implements InitializingBean {

    @Autowired
    private AppConfig config;

    @Autowired
    private BasicDAO dao;

    private UserLoader<T> userLoader;

    @Override
    public void afterPropertiesSet() throws Exception {
        configSession(userLoader());
    }

    public AppConfig config() {
        return config;
    }

    public BasicDAO dao() {
        return dao;
    }

    protected abstract void configSession(UserLoader<T> userLoader);

    /**
     * 返回用户加载器的实现类，由子类实现
     * @return
     */
    public UserLoader<T> userLoader() {
        if (userLoader == null) {
            synchronized (this) {
                UserLoader<T> ul = userLoader;
                if (ul == null) {
                    ul = createUserLoader();
                }
                userLoader = ul;
            }
        }
        return userLoader;
    }


    protected abstract UserLoader<T> createUserLoader();


    private Class<T> userType;

    public Class<T> userType() {
        if (this.userType != null) return userType;
        userType = (Class<T>) ClassUtils.getFirstParameterizedType(this.getClass());
        return userType;
    }

    public void updateLastLoginTime(String username) {
        SessionUser<T> user = getUserLoader().getUserFromCache(username);
        if (user != null) {
            user.setLastLoginTime(System.currentTimeMillis());
        }
    }

    /**
     * 更新用户信息
     * @param username
     */
    public void updateUser(String username){
        SessionUser<T> user = this.userLoader.loadUser(username);
        Assert.notNull(user, USERNAME_OR_PASSWORD_INCORRECT);
        SessionUser<T> loginUser = new SessionUser(user);
        loginUser.setOriginUser(user.getOriginUser());
        this.userLoader.setUserCache(loginUser);
    }

    // 解决框架的一个小BUG
    @Override
    public String decryptToken(String token) {
        try {
            return super.decryptToken(token);
        } catch (Exception e) {
            throw new ServiceException(NO_SIGN_IN);
        }
    }

    // 强制设置Cookie的生命周期为session
    @Override
    protected Cookie createSigninCookie(String token) {
        Cookie c = super.createSigninCookie(token);
        c.setHttpOnly(true);
        c.setMaxAge(-1);
        return c;
    }
}
