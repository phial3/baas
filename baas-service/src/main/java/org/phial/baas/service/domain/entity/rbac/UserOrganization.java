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
@Table(value = "baas_user_organization",
        indexes = {
                @Index(value = "idx_user_id", columns = @IndexColumn("userId"), type = IndexType.NULL),
        },
        comment = "用户组织关系表"
)
public class UserOrganization extends LongEditableEntity {

    @Column(comment = "用户ID主键", type = DataType.BIGINT)
    private Long userId;

    @Column(comment = "组织机构ID主键", type = DataType.BIGINT)
    private Long orgId;
}
