package org.phial.baas.service.domain.entity.system;

import org.phial.baas.service.domain.entity.rbac.SysUser;

import java.util.Map;

/**
 * 用户设置
 * @since 2021/4/8
 * @author mayanjun
 */
public class UserProfile {

    private SysUser user;

    private Map<String, String> settings;

    public UserProfile(SysUser user, Map<String, String> settings) {
        this.user = user;
        this.settings = settings;
    }

    /**
     * 获取 user
     *
     * @return user
     */
    public SysUser getUser() {
        return user;
    }

    /**
     * 设置 user
     *
     * @param user user 值
     */
    public void setUser(SysUser user) {
        this.user = user;
    }

    /**
     * 获取 settings
     *
     * @return settings
     */
    public Map<String, String> getSettings() {
        return settings;
    }

    /**
     * 设置 settings
     *
     * @param settings settings 值
     */
    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }
}
