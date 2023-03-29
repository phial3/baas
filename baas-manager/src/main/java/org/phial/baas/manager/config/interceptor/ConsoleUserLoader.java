package org.phial.baas.manager.config.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.mayanjun.core.Assert;
import org.mayanjun.mybatisx.api.query.Query;
import org.mayanjun.mybatisx.api.query.QueryBuilder;
import org.mayanjun.mybatisx.api.query.SortDirection;
import org.mayanjun.mybatisx.dal.dao.BasicDAO;
import org.mayanjun.myrest.session.AbstractSession;
import org.phial.baas.service.domain.entity.system.Menu;
import org.phial.baas.service.domain.entity.system.Privilege;
import org.phial.baas.service.domain.entity.system.RoleMenu;
import org.phial.baas.service.domain.entity.system.RolePrivilege;
import org.phial.baas.service.domain.entity.rbac.SysUser;
import org.phial.baas.service.domain.entity.rbac.UserRole;

import java.util.*;

public class ConsoleUserLoader extends DefaultUserLoader<SysUser> {

    public ConsoleUserLoader(ConfigurableSession<SysUser> session) {
        super(session);
    }

    private List<Menu> allMenus() {
        return Menu.hierarchicalMenus(
                session().dao().query(
                        QueryBuilder.custom(Menu.class).orderBy("id", SortDirection.DESC).build()
                )
        );
    }

    @Override
    protected SysUser queryUser(String username) {

        BasicDAO dao = session().dao();

        Query<SysUser> query = QueryBuilder.custom(SysUser.class)
                .andEquivalent("username", username)
                .build();
        SysUser user = dao.queryOne(query);
        Assert.notNull(user, AbstractSession.USERNAME_OR_PASSWORD_INCORRECT);

        // 查询角色列表
        // is admin
        if (Boolean.TRUE.equals(user.getAdministrator())) {
            user.setMenus(allMenus());
        } else {
            List<UserRole> userRoles = dao.query(
                    QueryBuilder.custom(UserRole.class)
                            .andEquivalent("user", user.getId())
                            .build()
            );
            Set<String> privileges = new HashSet<>();
            Map<Long, Menu> menuMap = new HashMap<>();
            userRoles.forEach(userRole -> {
                Long roleId = userRole.getRole().getId();

                // load menus
                List<RoleMenu> roleMenus = dao.query(
                        QueryBuilder.custom(RoleMenu.class).andEquivalent("role", roleId).build()
                );
                roleMenus.forEach(rm -> {
                    Menu menu = dao.getInclude(rm.getMenu());
                    if (menu != null) {
                        menuMap.put(menu.getId(), menu);

                        Long pid = menu.getParentId();
                        if (pid != null && pid > 0) {
                            Menu pmenu = menuMap.get(pid);
                            if (pmenu == null) menuMap.put(pid, dao.getInclude(new Menu(pid)));
                        }
                    }

                });

                // load privileges
                List<RolePrivilege> rolePrivileges = dao.query(
                        QueryBuilder.custom(RolePrivilege.class).andEquivalent("role", roleId).build()
                );
                rolePrivileges.forEach(rolePrivilege -> {
                    Long pid = rolePrivilege.getPrivilege().getId();
                    Privilege p = dao.getInclude(new Privilege(pid), "method", "dependencies");
                    if (p != null) {
                        privileges.add(p.getMethod());
                        String de = p.getDependencies();
                        if (StringUtils.isNotBlank(de)) {
                            String[] des = de.split(",");
                            privileges.addAll(Arrays.asList(des));
                        }
                    }
                });
            });
            user.setPrivileges(privileges);
            user.setMenus(Menu.hierarchicalMenus(menuMap.values()));
        }
        return user;
    }
}
