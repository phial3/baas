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
 * 角色菜单映射
 * @since 2021/4/8
 * @author mayanjun
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "t_role_menu",
        indexes = {
                @Index(value = "idx_role", columns = @IndexColumn("role")),
                @Index(value = "idx_menu", columns = @IndexColumn("menu"))
        },
        comment = "角色权限表")
public class RoleMenu extends LongEditableEntity {

    @Column(type = DataType.BIGINT, referenceField = "id")
    private Role role;

    @Column(type = DataType.BIGINT, referenceField = "id")
    private Menu menu;

    public RoleMenu() {
    }

    public RoleMenu(Long id) {
        super(id);
    }

    public RoleMenu(Role role, Menu menu) {
        this.role = role;
        this.menu = menu;
    }

    public RoleMenu(Long roleId, Long menuId) {
        this.role = new Role(roleId);
        this.menu = new Menu(menuId);
    }
}
