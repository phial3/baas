package org.phial.baas.manager.controller.system;

import org.mayanjun.mybatisx.api.query.SortDirection;
import org.mayanjun.myrest.RestResponse;
import org.phial.baas.manager.config.init.Dependency;
import org.phial.baas.manager.config.init.MetaProperty;
import org.phial.baas.manager.config.init.Privileged;
import org.phial.baas.manager.config.init.PrivilegedMeta;
import org.phial.baas.manager.config.init.Profiler;
import org.phial.baas.manager.config.interceptor.Login;
import org.phial.baas.manager.controller.DataController;
import org.phial.baas.manager.service.ConsoleBaseBusiness;
import org.phial.baas.manager.service.ParametersBuilder;
import org.phial.baas.manager.service.system.RoleBusiness;
import org.phial.baas.service.domain.entity.rbac.Role;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 角色管理接口
 * @since 2019-10-10
 * @author mayanjun
 */
@Login
@RestController
@RequestMapping("api/role")
@PrivilegedMeta(@MetaProperty(name = "module", value = "平台角色管理"))
public class RoleController extends DataController<Role> {

    private RoleBusiness business;

    @Override
    protected ConsoleBaseBusiness<Role> business() {
        return business;
    }

    public RoleController(RoleBusiness business) {
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
                       @RequestParam(required = false) String sname) {
        ParametersBuilder pb = ParametersBuilder.custom(orderField, orderDirection);
        pb.add("id", sid);
        pb.add("__LIKE__name", sname);
        return RestResponse.ok()
                .add(RESP_KEY_LIST, business.list(page, pageSize, pb))
                .add(RESP_KEY_TOTAL, business.count(pb));
    }

    @Profiler
    @Privileged("查询所有{module}")
    @GetMapping("all-roles")
    public Object listAllRoles() {
        List<Role> all = business.listAll(null);
        return RestResponse.ok().add(RESP_KEY_LIST, all);
    }

    @Profiler
    @PostMapping
    @Privileged(value = "创建{module}",
            dependencies = {
                    @Dependency(type = PrivilegeController.class, method = "listAllPrivileges"),
                    @Dependency(type = MenuController.class, method = "listAllMenus")
            }
    )
    @Override
    public Object save(@RequestBody Role bean) {
        return super.save(bean);
    }

    @Profiler
    @PostMapping("update")
    @Privileged(value = "更新{module}",
            dependencies = {
                    @Dependency(type = PrivilegeController.class, method = "listAllPrivileges"),
                    @Dependency(type = MenuController.class, method = "listAllMenus")
            }
    )
    @Override
    public Object update(@RequestBody Role bean) {
        return super.update(bean);
    }



}
