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
@Table(value = "t_role_permission",
        indexes = {
                @Index(value = "idx_role", columns = @IndexColumn("role"), type = IndexType.NULL),
        },
        comment = "角色权限关系表"
)
public class RolePermission extends LongEditableEntity {

    @Column(type = DataType.BIGINT, referenceField = "id")
    private Role role;

    @Column(type = DataType.BIGINT, referenceField = "id")
    private Permission permission;

    public RolePermission(){}

    public RolePermission(Long id) {
        super(id);
    }

    public RolePermission(Role role, Permission permission) {
        this.role = role;
        this.permission = permission;
    }

    public RolePermission(Long roleId, Long permissionId) {
        this.role = new Role(roleId);
        this.permission = new Permission(permissionId);
    }
}
