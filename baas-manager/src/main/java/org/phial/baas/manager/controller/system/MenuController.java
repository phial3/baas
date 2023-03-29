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
import org.phial.baas.manager.service.system.MenuBusiness;
import org.phial.baas.service.domain.entity.system.Menu;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 菜单管理接口
 * @since 2019-10-10
 * @author mayanjun
 */
@Login
@RestController
@RequestMapping("api/menu")
@PrivilegedMeta({
        @MetaProperty(name = "module", value = "菜单")
})
public class MenuController extends DataController<Menu> {

    private MenuBusiness business;

    public MenuController(MenuBusiness business) {
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
        return RestResponse.ok().add(RESP_KEY_LIST, business.listAll(pb));
    }

    @Profiler
    @Privileged("查询所有根{module}")
    @GetMapping("all-root-menus")
    public Object listAllRootMenus(@RequestParam(required = false) Long excludeId) {
        ParametersBuilder pb = ParametersBuilder.custom();
        pb.add("parentId", 0);
        pb.add("type", Menu.MenuType.LINK);
        pb.add("__!=__id", excludeId);

        List<Menu> all = business.listAll(pb);
        return RestResponse.ok().add(RESP_KEY_LIST, all);
    }

    @Profiler
    @Privileged("查询所有{module}")
    @GetMapping("all-menus")
    public Object listAllMenus() {
        List<Menu> all = business.listAll(null);
        return RestResponse.ok().add(RESP_KEY_LIST, all);
    }

    @Profiler
    @PostMapping
    @Privileged(value = "创建{module}", dependencies = @Dependency(type = MenuController.class, method = "listAllRootMenus"))
    @Override
    public Object save(@RequestBody Menu bean) {
        return super.save(bean);
    }

    @Profiler
    @PostMapping("update")
    @Privileged(value = "更新{module}", dependencies = @Dependency(type = MenuController.class, method = "listAllRootMenus"))
    @Override
    public Object update(@RequestBody Menu bean) {
        return super.update(bean);
    }

    @Profiler
    @GetMapping("queryOneByParentId")
    public Object queryOneByParentId(String name,boolean isAdd,Long id,boolean isParent) {
        return RestResponse.ok().add(RESP_KEY_LIST, business.queryOneByParentId(name,isAdd,id,isParent));
    }

    @Override
    protected ConsoleBaseBusiness<Menu> business() {
        return business;
    }
}
