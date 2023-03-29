package org.phial.baas.manager.service.system;

import org.mayanjun.core.Assert;
import org.mayanjun.mybatisx.api.query.QueryBuilder;
import org.phial.baas.service.domain.entity.system.App;
import org.phial.baas.manager.service.ConsoleBaseBusiness;
import org.phial.baas.service.util.Strings;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AppBusiness extends ConsoleBaseBusiness<App> {

    @Override
    protected void doCheck(App entity, boolean update) {
        Assert.notBlank(entity.getName(), "名称不能为空");
        if (!update) {
            entity.setSecretKey(Strings.secretKey(32));
        }
    }

    @Override
    protected List<App> doQuery(QueryBuilder<App> builder) {
        List<App> list = super.doQuery(builder);
        list.forEach(e -> {
            e.setSecretKey(Strings.escape(e.getSecretKey()));
        });
        return list;
    }

    public String secretKey(Long id) {
        Assert.greatThanZero(id, "APP ID错误");
        App app = service.getInclude(new App(id), "secretKey");
        Assert.notNull(app, "APP不存在");
        return app.getSecretKey();
    }
}