package org.phial.baas.manager.controller.system;


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
import org.phial.baas.manager.service.system.AppBusiness;
import org.phial.baas.service.domain.entity.system.App;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 应用
 * @since 2019-10-10
 * @author mayanjun
 */
@Login
@RestController
@RequestMapping("api/app")
@PrivilegedMeta({
        @MetaProperty(name = "module", value = "应用")
})
public class AppController extends DataController<App> {

    private AppBusiness business;

    public AppController(AppBusiness business) {
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
    @GetMapping("secret-key")
    public Object showSecretKey(Long id) {
        return RestResponse.ok().add("secretKey", business.secretKey(id));
    }

    @Profiler
    @Override
    protected ConsoleBaseBusiness<App> business() {
        return business;
    }
}