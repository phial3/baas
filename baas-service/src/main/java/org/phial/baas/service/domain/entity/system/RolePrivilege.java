package org.phial.baas.service.domain.entity.system;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mayanjun.mybatisx.api.annotation.Column;
import org.mayanjun.mybatisx.api.annotation.Index;
import org.mayanjun.mybatisx.api.annotation.IndexColumn;
import org.mayanjun.mybatisx.api.annotation.Table;
import org.mayanjun.mybatisx.api.entity.LongEditableEntity;
import org.mayanjun.mybatisx.api.enums.DataType;
import org.phial.baas.service.domain.entity.rbac.Role;

/**
 * 角色权限映射
 * @since 2021/4/8
 * @author mayanjun
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "t_role_privilege",
        indexes = {
                @Index(value = "idx_role", columns = @IndexColumn("role")),
                @Index(value = "idx_privilege", columns = @IndexColumn("privilege"))
        },
        comment = "角色权限表")
public class RolePrivilege extends LongEditableEntity {

    @Column(type = DataType.BIGINT, referenceField = "id")
    private Role role;

    @Column(type = DataType.BIGINT, referenceField = "id")
    private Privilege privilege;

    public RolePrivilege() {
    }

    public RolePrivilege(Long id) {
        super(id);
    }

    public RolePrivilege(Role role, Privilege privilege) {
        this.role = role;
        this.privilege = privilege;
    }

    public RolePrivilege(Long roleId, Long privilegeId) {
        this.role = new Role(roleId);
        this.privilege = new Privilege(privilegeId);
    }
}
