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
@Table(value = "baas_permission",
        indexes = {},
        comment = "权限表"
)
public class Permission extends NamedEntity {

    @Column(comment = "用户ID", type = DataType.BIGINT)
    private Long userId;

    @Column(comment = "租户ID", type = DataType.VARCHAR, length = "32")
    private String tenantId;

}
