package org.phial.baas.manager.service.system;

import org.mayanjun.core.Assert;
import org.mayanjun.mybatisx.api.query.QueryBuilder;
import org.phial.baas.service.domain.entity.sys.Privilege;
import org.phial.baas.manager.service.ConsoleBaseBusiness;
import org.springframework.stereotype.Component;

/**
 * 权限管理
 * @since 2019-07-06
 * @author mayanjun
 */
@Component
public class PrivilegeBusiness extends ConsoleBaseBusiness<Privilege> {

    @Override
    protected void doCheck(Privilege entity, boolean update) {
        Assert.notBlank(entity.getName(), "角色名称不能为空");
    }

    @Override
    protected void renderListAllBuilder(QueryBuilder<Privilege> builder) {
        builder.excludeFields("method", "dependencies");
    }

    protected String[] formatExportEntityHeaders(Class<Privilege> privilegeClass) {
        return new String[]{"修改时间","创建时间","权限ID","权限名称", "权限方法", "权限备注", "权限依赖"};
    }
}
