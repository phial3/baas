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
@Table(value = "t_role_resource",
        indexes = {
                @Index(value = "idx_role", columns = @IndexColumn("role"), type = IndexType.NULL),
        },
        comment = "角色资源关系表"
)
public class RoleResource extends LongEditableEntity {

    @Column(type = DataType.BIGINT, referenceField = "id")
    private Role role;

    @Column(type = DataType.BIGINT, referenceField = "id")
    private SysResource resource;


    public RoleResource(){}

    public RoleResource(Long id) {
        super(id);
    }

    public RoleResource(Role role, SysResource resource) {
        this.role = role;
        this.resource = resource;
    }

    public RoleResource(Long roleId, Long resourceId) {
        this.role = new Role(roleId);
        this.resource = new SysResource(resourceId);
    }
}
