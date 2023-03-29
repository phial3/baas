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
@Table(value = "t_user_role",
        indexes = {
                @Index(value = "idx_user", columns = @IndexColumn("user"), type = IndexType.NULL),
                @Index(value = "idx_role", columns = @IndexColumn("role"), type = IndexType.NULL),
        },
        comment = "用户角色关系表"
)
public class UserRole extends LongEditableEntity {

    @Column(type = DataType.BIGINT, referenceField = "id")
    private SysUser user;

    @Column(type = DataType.BIGINT, referenceField = "id")
    private Role role;

    public UserRole() {
    }

    public UserRole(Long id) {
        super(id);
    }

    public UserRole(SysUser user, Role role) {
        this.user = user;
        this.role = role;
    }

    public UserRole(Long userId, Long roleId) {
        this.user = new SysUser(userId);
        this.role = new Role(roleId);
    }
}
