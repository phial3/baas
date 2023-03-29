package org.phial.baas.manager.controller.system;

import org.mayanjun.myrest.BaseController;
import org.mayanjun.myrest.RestResponse;
import org.mayanjun.myrest.session.SessionUser;
import org.phial.baas.manager.config.init.MetaProperty;
import org.phial.baas.manager.config.init.Privileged;
import org.phial.baas.manager.config.init.PrivilegedMeta;
import org.phial.baas.manager.config.init.Profiler;
import org.phial.baas.manager.config.interceptor.ConsoleSessionManager;
import org.phial.baas.manager.config.interceptor.Login;
import org.phial.baas.manager.service.system.AttributeBusiness;
import org.phial.baas.service.domain.entity.system.Settings;
import org.springframework.web.bind.annotation.*;

/**
 * 系统设置接口
 * @since 2019-10-10
 * @author mayanjun
 */
@Login
@RequestMapping("api/settings")
@RestController
@PrivilegedMeta(@MetaProperty(name = "module", value = "系统设置"))
public class SettingsController extends BaseController {

    private AttributeBusiness business;

    private ConsoleSessionManager sessionManager;

    public SettingsController(AttributeBusiness business, ConsoleSessionManager sessionManager) {
        this.business = business;
        this.sessionManager = sessionManager;
    }

    @Profiler
    @GetMapping("{id}")
    @Privileged("获取{module}详细数据")
    public Object get(@PathVariable long id) {
        SessionUser user = sessionManager.getCurrentUser();
        return RestResponse.ok().add("entity", business.allSettings(user.getUsername()));
    }

    @Profiler
    @Privileged("更新{module}")
    @PostMapping("update")
    public Object update(@RequestBody Settings bean) {
        business.updateSettings(bean);
        return RestResponse.ok();
    }

    @Profiler
    @Privileged("恢复出厂设置")
    @PostMapping("factory")
    public Object restoreFactorySettings() {
        business.restoreFactorySettings();
        return RestResponse.ok();
    }
}
