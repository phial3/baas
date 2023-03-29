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
import org.phial.baas.manager.service.system.RegionBusiness;
import org.phial.baas.service.domain.entity.system.Region;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @since 2019-07-10
 * @author mayanjun
 */
@Login
@RestController
@RequestMapping("api/region")
@PrivilegedMeta({
        @MetaProperty(name = "module", value = "地区")
})
public class RegionController extends DataController<Region> {

    private RegionBusiness business;

    @Override
    protected ConsoleBaseBusiness<Region> business() {
        return business;
    }

    public RegionController(RegionBusiness business) {
        this.business = business;
    }

    @Profiler
    @Privileged("{module}列表查询")
    @GetMapping
    public Object list(@RequestParam(required = false, defaultValue = "1") Integer page,
                       @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                       @RequestParam(required = false, defaultValue = "id") String orderField,
                       @RequestParam(required = false, defaultValue = "DESC") SortDirection orderDirection,
                       @RequestParam(required = false) Long sid,
                       @RequestParam(required = false) String sname,
                       @RequestParam(required = false) Long sparent,
                       @RequestParam(required = false) Integer slevel) {
        ParametersBuilder pb = ParametersBuilder.custom(orderField, orderDirection);
        pb.add("id", sid);
        pb.add("parent", sparent);
        pb.add("__LIKE__name", sname);
        pb.add("level", slevel);
        return RestResponse.ok().add(RESP_KEY_LIST, business.listAll(pb));
    }


}
