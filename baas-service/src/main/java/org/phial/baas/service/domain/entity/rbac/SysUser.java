package org.phial.baas.service.domain.entity.rbac;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.mayanjun.mybatisx.api.annotation.Column;
import org.mayanjun.mybatisx.api.annotation.Index;
import org.mayanjun.mybatisx.api.annotation.IndexColumn;
import org.mayanjun.mybatisx.api.annotation.Table;
import org.mayanjun.mybatisx.api.enums.DataType;
import org.mayanjun.mybatisx.api.enums.IndexType;
import org.phial.baas.service.domain.entity.sys.AbstractUser;
import org.phial.baas.service.domain.entity.sys.Menu;

import java.util.List;
import java.util.Set;


/**
 * 用户
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Table(value = "baas_user",
        indexes = {
                @Index(value = "idx_username", columns = @IndexColumn(value = "username", length = 32), type = IndexType.UNIQUE)
        },
        comment = "平台用户账号管理")
public class SysUser extends AbstractUser {

    private List<Menu> menus;
    private Set<String> privileges;

    public SysUser() {
    }

    public SysUser(Long id) {
        super(id);
    }

    public SysUser(String username) {
        super(username);
    }

    public SysUser(String username, long loginTime) {
        super(username, loginTime);
    }

    public SysUser(String username, String password) {
        super(username, password);
    }


    @Column(comment = "是否管理员", type = DataType.BIT, length = "1")
    private Boolean administrator;

    /**
     * 角色列表
     */
    private Long[] roles;

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }

    public List<Menu> getMenus() {
        return menus;
    }

    public void setPrivileges(Set<String> privileges) {
        this.privileges = privileges;
    }

    public Set<String> getPrivileges() {
        return privileges;
    }
}
