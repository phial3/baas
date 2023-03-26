package org.phial.baas.manager.config.interceptor;


import org.phial.baas.service.domain.entity.rbac.SysUser;

public class MobileUserLoader extends DefaultUserLoader<SysUser> {

    public MobileUserLoader(ConfigurableSession<SysUser> session) {
        super(session);
    }
}
