package org.phial.baas.manager.controller.system;

import org.mayanjun.mybatisx.api.query.SortDirection;
import org.mayanjun.myrest.RestResponse;
import org.phial.baas.manager.config.init.Dependency;
import org.phial.baas.manager.config.init.MetaProperty;
import org.phial.baas.manager.config.init.Privileged;
import org.phial.baas.manager.config.init.PrivilegedMeta;
import org.phial.baas.manager.config.init.Profiler;
import org.phial.baas.manager.config.interceptor.ConsoleSessionManager;
import org.phial.baas.manager.config.interceptor.Login;
import org.phial.baas.manager.controller.DataController;
import org.phial.baas.manager.service.ConsoleBaseBusiness;
import org.phial.baas.manager.service.ParametersBuilder;
import org.phial.baas.manager.service.system.UserBusiness;
import org.phial.baas.service.domain.entity.rbac.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户管理接口
 * @author mayanjun
 * @since 2019-10-10
 */
@Login
@RestController
@RequestMapping("api/user")
@PrivilegedMeta(@MetaProperty(name = "module", value = "平台账号管理"))
public class UserController extends DataController<SysUser> {

    private UserBusiness business;

    @Override
    protected ConsoleBaseBusiness<SysUser> business() {
        return business;
    }

    public UserController(UserBusiness business) {
        this.business = business;
    }

    @Profiler
    @Privileged("{module}列表查询")
    @GetMapping
    public Object list(@RequestParam(required = false, defaultValue = "0") Integer page,
                       @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                       @RequestParam(required = false, defaultValue = "id") String orderField,
                       @RequestParam(required = false, defaultValue = "DESC") SortDirection orderDirection,
                       @RequestParam(required = false) Long sid,
                       @RequestParam(required = false) String susername,
                       @RequestParam(required = false) String phone,
                       @RequestParam(required = false) Long entityId,
                       @RequestParam(required = false) String email,
                       @RequestParam(required = false) String apiType,
                       @RequestParam(required = false) String name) {
        ParametersBuilder pb = ParametersBuilder.custom(orderField, orderDirection);
        pb.add("id", sid);
        pb.add("__LIKE__username", susername);
        pb.add("__LIKE__name", name);
        pb.add("__LIKE__phone", phone);
        pb.add("entityId", entityId);
        pb.add("__LIKE__email", email);
        if("AlarmSetting".equals(apiType)){
            pb.add("enabled", Boolean.TRUE);
        }
        if (!getUser().getUsername().equals("admin")) {
            pb.add("__!=__username", "admin");
        }

        return RestResponse.ok()
                .add("list", business.list(page, pageSize, pb))
                .add("total", business.count(pb));
    }

    @Profiler
    @GetMapping("secret-key")
    public Object showSecretKey(Long id) {
        return RestResponse.ok().add("secretKey", business.secretKey(id));
    }

    @Profiler
    @PostMapping
    @Privileged(value = "创建{module}", dependencies = @Dependency(type = RoleController.class, method = "listAllRoles"))
    @Override
    public Object save(@RequestBody SysUser bean) {
        return RestResponse.ok().add("entity", business.save(bean));
    }

    @Autowired
    private ConsoleSessionManager sessionManager;

    @Profiler
    @PostMapping("update")
    @Privileged(value = "更新{module}", dependencies = @Dependency(type = RoleController.class, method = "listAllRoles"))
    @Override
    public Object update(@RequestBody SysUser bean) {
        business.update(bean);
        return RestResponse.ok();
    }

    @Profiler
    @GetMapping("all")
    public Object all(){
        return RestResponse.ok().add(RESP_KEY_LIST, business.all());
    }

    @Profiler
    @PostMapping("updatePassword")
    public Object updatePassword(@RequestBody SysUser user, HttpServletRequest request, HttpServletResponse response){
        business.updatePassword(user);
        sessionManager.signOut(request, response);
        return RestResponse.ok();
    }

    @Profiler
    @PostMapping("equalsPassword")
    public Object equalsPassword(@RequestBody SysUser user){
        business.equalsPassword(user.getPassword());
        return RestResponse.ok();
    }

}
