package org.phial.baas.service.domain.entity.rbac;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.mayanjun.mybatisx.api.annotation.Column;
import org.mayanjun.mybatisx.api.annotation.Index;
import org.mayanjun.mybatisx.api.annotation.IndexColumn;
import org.mayanjun.mybatisx.api.annotation.Table;
import org.mayanjun.mybatisx.api.entity.LongEditableEntity;
import org.mayanjun.mybatisx.api.enums.DataType;
import org.mayanjun.mybatisx.api.enums.IndexType;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Table(value = "baas_role_resource",
        indexes = {
                @Index(value = "idx_role_id", columns = @IndexColumn("roleId"), type = IndexType.NULL),
        },
        comment = "角色资源关系表"
)
public class RoleResource extends LongEditableEntity {
    @Column(comment = "角色ID主键", type = DataType.BIGINT)
    private Long roleId;

    @Column(comment = "资源ID主键", type = DataType.BIGINT)
    private Long resourceId;
}
