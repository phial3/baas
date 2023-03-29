package org.phial.baas.manager.service.system;

import org.mayanjun.core.Assert;
import org.mayanjun.mybatisx.api.entity.Entity;
import org.mayanjun.mybatisx.api.query.Query;
import org.mayanjun.mybatisx.api.query.QueryBuilder;
import org.phial.baas.manager.service.ConsoleBaseBusiness;
import org.phial.baas.manager.service.EntityEvent;
import org.phial.baas.service.domain.entity.system.Apk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApkBusiness extends ConsoleBaseBusiness<Apk> {


    @Override
    protected void doCheck(Apk entity, boolean update) {
        Assert.notBlank(entity.getName(), "名称不能为空");
        Assert.notBlank(entity.getPkgName(), "应用包名不能为空");
        Assert.greatThanZero(entity.getCode(), "应用编码不能为空");
        Assert.notBlank(entity.getFile(), "请上传apk应用文件");
        if (!update) {

        }
    }
    public Apk checkUpdate(Integer code,String packageName){
        Query<Apk> apkQuery = QueryBuilder.custom(Apk.class)
                .andEquivalent("enabled",Boolean.TRUE)
                .andEquivalent("packageName",packageName)
                .andGreaterThan("code",code).build();
        Apk apk = service.queryOne(apkQuery);
        return apk;
    }

    @Override
    public void onEntityChange(EntityEvent event) {
        super.onEntityChange(event);
        EntityEvent.EventType type = event.type();

        switch (type) {
            case NEW:
            case UPDATE:
                Entity [] entities = event.entities();
                if (entities != null && entities.length > 0) {
                    for (Entity entity : entities) {
                    }
                }
                break;
        }
    }
}