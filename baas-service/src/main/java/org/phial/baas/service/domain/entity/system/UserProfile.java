package org.phial.baas.service.domain.entity.system;

import lombok.Data;
import org.phial.baas.service.domain.entity.rbac.SysUser;

import java.util.Map;

/**
 * 用户设置
 * @since 2021/4/8
 * @author mayanjun
 */
@Data
public class UserProfile {

    private SysUser user;

    private Map<String, String> settings;

    public UserProfile(SysUser user, Map<String, String> settings) {
        this.user = user;
        this.settings = settings;
    }
}
