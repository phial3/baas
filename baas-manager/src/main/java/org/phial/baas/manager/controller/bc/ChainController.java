package org.phial.baas.manager.controller.bc;

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
import org.phial.baas.manager.service.bc.ChainBusiness;
import org.phial.baas.manager.util.StatusCode;
import org.phial.baas.service.domain.entity.bc.Chain;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author admin
 */
@Login
@RestController
@RequestMapping("api/chain")
@PrivilegedMeta({
        @MetaProperty(name = "module", value = "联盟通道管理")
})
public class ChainController extends DataController<Chain> {

    @Resource
    private ChainBusiness chainBusiness;

    @Override
    protected ConsoleBaseBusiness<Chain> business() {
        return chainBusiness;
    }


    @Profiler
    @Privileged(value = "{module}列表查询")
    @GetMapping
    public Object list(@RequestParam(required = false, defaultValue = "0") Integer page,
                       @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                       @RequestParam(required = false, defaultValue = "false") Boolean export,
                       @RequestParam(required = false, defaultValue = "id") String orderField,
                       @RequestParam(required = false, defaultValue = "DESC") SortDirection orderDirection,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date date0,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date date1,
                       @RequestParam(required = false) Long id,
                       @RequestParam(required = false) String name,
                       @RequestParam(required = false) String chainId,
                       @RequestParam(required = false) Long[] ids
    ) {

        ParametersBuilder builder = ParametersBuilder.custom(orderField, orderDirection);
        builder.add("id", id)
                .add("__LIKE__name", name)
                .add("chainId", chainId);

        return RestResponse.ok()
                .add(RESP_KEY_LIST, business().list(page, pageSize, builder))
                .add(RESP_KEY_TOTAL, business().count(builder));
    }


    @Override
    @GetMapping("{id}")
    public Object get(long id) {
        return super.get(id);
    }

    @Profiler
    @Override
    @PostMapping("delete")
    public Object delete(Long[] ids) {
        return super.delete(ids);
    }

    @Profiler
    @Override
    @PostMapping
    public Object save(Chain bean) {
        return super.save(bean);
    }

    @Profiler
    @Override
    @PostMapping("update")
    public Object update(Chain bean) {
        return super.update(bean);
    }
}
