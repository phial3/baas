package org.phial.baas.service.domain.entity.sys;

import org.mayanjun.mybatisx.api.annotation.Column;
import org.mayanjun.mybatisx.api.annotation.Index;
import org.mayanjun.mybatisx.api.annotation.IndexColumn;
import org.mayanjun.mybatisx.api.annotation.Table;
import org.mayanjun.mybatisx.api.entity.LongEditableEntity;
import org.mayanjun.mybatisx.api.enums.DataType;

/**
 * 菜单关联的菜单
 *
 * @author mayanjun
 * @since 2021/12/6
 */
@Table(value = "t_menu_privilege",
        indexes = {
                @Index(value = "idx_role", columns = @IndexColumn("menu")),
                @Index(value = "idx_privilege", columns = @IndexColumn("privilege"))
        },
        comment = "菜单权限表")
public class MenuPrivilege extends LongEditableEntity {

    @Column(type = DataType.BIGINT, referenceField = "id")
    private Menu menu;

    @Column(type = DataType.BIGINT, referenceField = "id")
    private Privilege privilege;

    public MenuPrivilege() {
    }

    public MenuPrivilege(Long id) {
        super(id);
    }

    public MenuPrivilege(Menu menu, Privilege privilege) {
        this.menu = menu;
        this.privilege = privilege;
    }

    public MenuPrivilege(Long menuId, Long privilegeId) {
        this.menu = new Menu(menuId);
        this.privilege = new Privilege(privilegeId);
    }

    /**
     * 获取 menu
     *
     * @return menu
     */
    public Menu getMenu() {
        return menu;
    }

    /**
     * 设置 menu
     *
     * @param menu menu 值
     */
    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public Privilege getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Privilege privilege) {
        this.privilege = privilege;
    }
}
