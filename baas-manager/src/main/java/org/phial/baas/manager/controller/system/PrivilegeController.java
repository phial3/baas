package org.phial.baas.manager.controller.system;

import org.mayanjun.core.ServiceException;
import org.mayanjun.mybatisx.api.query.SortDirection;
import org.mayanjun.myrest.RestResponse;
import org.phial.baas.manager.config.init.MetaProperty;
import org.phial.baas.manager.config.init.Privileged;
import org.phial.baas.manager.config.init.PrivilegedMeta;
import org.phial.baas.manager.config.init.Profiler;
import org.phial.baas.manager.config.interceptor.Login;
import org.phial.baas.manager.controller.DataController;
import org.phial.baas.manager.service.ConsoleBaseBusiness;
import org.phial.baas.manager.service.ParametersBuilder;
import org.phial.baas.manager.service.system.PrivilegeBusiness;
import org.phial.baas.manager.util.StatusCode;
import org.phial.baas.service.domain.entity.system.Privilege;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 权限管理接口
 * @since 2019-10-10
 * @author mayanjun
 */
@Login
@RestController
@RequestMapping("api/privilege")
@PrivilegedMeta(@MetaProperty(name = "module", value = "权限"))
public class PrivilegeController extends DataController<Privilege> {

    private PrivilegeBusiness business;

    public PrivilegeController(PrivilegeBusiness business) {
        this.business = business;
    }

    @Profiler
    @Privileged("{module}列表查询")
    @GetMapping
    public Object list(@RequestParam(required = false, defaultValue = "0") Integer page,
                       @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                       @RequestParam(required = false, defaultValue = "id") String orderField,
                       @RequestParam(required = false, defaultValue = "DESC") SortDirection orderDirection,
                       @RequestParam(required = false, defaultValue = "false") Boolean export,
                       @RequestParam(required = false) Long sid,
                       @RequestParam(required = false) String sname) {
        ParametersBuilder pb = ParametersBuilder.custom(orderField, orderDirection);
        pb.add("id", sid);
        pb.add("__LIKE__name", sname);

        if (Boolean.TRUE.equals(export)) {
            return super.exportData(pb);
        }

        return RestResponse.ok()
                .add("list", business.list(page, pageSize, pb))
                .add("total", business.count(pb));
    }

    @Profiler
    @Privileged("查询所有{module}")
    @GetMapping("all-privileges")
    public Object listAllPrivileges() {
        List<Privilege> all = business.listAll(null);
        return RestResponse.ok().add(RESP_KEY_LIST, all);
    }

    @Override
    protected ConsoleBaseBusiness<Privilege> business() {
        return business;
    }

    @Profiler
    @Override
    public Object get(long id) {
        throw new ServiceException(StatusCode.API_NOT_SUPPORTED);
    }

    @Profiler
    @Override
    public Object delete(Long[] ids) {
        throw new ServiceException(StatusCode.API_NOT_SUPPORTED);
    }

    @Profiler
    @Override
    public Object save(Privilege bean) {
        throw new ServiceException(StatusCode.API_NOT_SUPPORTED);
    }

    @Profiler
    @Override
    public Object update(Privilege bean) {
        throw new ServiceException(StatusCode.API_NOT_SUPPORTED);
    }
}
