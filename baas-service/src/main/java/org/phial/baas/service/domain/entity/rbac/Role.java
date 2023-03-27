package org.phial.baas.service.domain.entity.rbac;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.mayanjun.mybatisx.api.annotation.Column;
import org.mayanjun.mybatisx.api.annotation.Table;
import org.mayanjun.mybatisx.api.enums.DataType;
import org.phial.baas.service.domain.entity.NamedEntity;


@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Table(value = "baas_role",
        indexes = {},
        comment = "角色表"
)
public class Role extends NamedEntity {

    @Column(comment = "用户ID", type = DataType.BIGINT)
    private Long userId;

    @Column(comment = "租户ID", type = DataType.VARCHAR, length = "32")
    private String tenantId;

    public Role(){}

    public Role(Long id) {
        super(id);
    }

    //接收权限参数
    private Long[] privileges;
    private Long[] menus;
}
