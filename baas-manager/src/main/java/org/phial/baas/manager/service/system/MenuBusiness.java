package org.phial.baas.manager.service.system;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mayanjun.core.Assert;
import org.mayanjun.mybatisx.api.query.Query;
import org.mayanjun.mybatisx.api.query.QueryBuilder;
import org.phial.baas.service.domain.entity.sys.Menu;
import org.phial.baas.service.domain.entity.sys.MenuPrivilege;
import org.phial.baas.service.domain.entity.sys.Privilege;
import org.phial.baas.manager.service.ConsoleBaseBusiness;
import org.phial.baas.service.service.ParametersBuilder;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 菜单管理
 * @since 2019-07-06
 * @author mayanjun
 * @vendor JDD (https://www.jddglobal.com)
 */
@Component
public class MenuBusiness extends ConsoleBaseBusiness<Menu> {

    @Override
    protected void doCheck(Menu entity, boolean update) {
        Assert.notBlank(entity.getName(), "菜单名称不能为空");
    }

    @Override
    public List<Menu> listAll(ParametersBuilder parametersBuilder) {
        List<Menu> list = super.listAll(parametersBuilder);

        // 设置Menu列表的权限
        List<MenuPrivilege> menuPrivileges = service.query(QueryBuilder.custom(MenuPrivilege.class).build());

        Map<Long, List<Long>> menuPrivilegesMap = new HashMap<>();
        for (MenuPrivilege menuPrivilege : menuPrivileges) {
            menuPrivilegesMap.computeIfAbsent(menuPrivilege.getMenu().getId(), k -> new ArrayList<>()
            ).add(menuPrivilege.getPrivilege().getId());
        }

        for (Menu menu : list) {
            List<Long> privileges = menuPrivilegesMap.get(menu.getId());
            if (CollectionUtils.isNotEmpty(privileges)) {
                Long[] ps = new Long[privileges.size()];
                privileges.toArray(ps);
                menu.setPrivileges(ps);
            }
        }

        return Menu.hierarchicalMenus(list);
    }


    @Override
    public void delete(Long[] ids) {
        transaction().execute(transactionStatus -> {
            super.delete(ids);
            for (Long id : ids) {
                service.delete(
                        QueryBuilder.custom(Menu.class)
                                .andEquivalent("parentId", id)
                                .build()
                );
            }
            return true;
        });
    }

    @Override
    protected Menu doGet(Menu bean) {
        Menu menu = super.doGet(bean);
        // 加载权限列表
        Query<MenuPrivilege> query = QueryBuilder.custom(MenuPrivilege.class)
                .andEquivalent("menu", bean.getId())
                .build();
        List<MenuPrivilege> menuPrivileges = service.query(query);
        menu.setPrivileges(ids(menuPrivileges, e -> ((MenuPrivilege)e).getPrivilege().getId()));
        return menu;
    }

    @Override
    protected long doSave(Menu bean) {
        return transaction().execute(status -> {
            Long rid = super.doSave(bean);
            saveMenuPrivileges(bean);
            return rid;
        });
    }

    @Override
    protected int doUpdate(Menu bean) {
        return transaction().execute(status -> {
            Query<MenuPrivilege> query = QueryBuilder.custom(MenuPrivilege.class)
                    .andEquivalent("menu", bean.getId())
                    .build();
            service.delete(query);
            saveMenuPrivileges(bean);
            return super.doUpdate(bean);
        });
    }

    private void saveMenuPrivileges(Menu menu) {
        Long pids[] = menu.getPrivileges();
        if (pids != null && pids.length > 0) {
            Set<Long> dependencies = new HashSet<>();
            for (Long pid : pids) {
                dependencies.add(pid);
                // 权限所依赖的权限也应该被分配
                Privilege privilege = service.getInclude(new Privilege(pid), "dependencies");
                String csps = privilege.getDependencies();
                if (StringUtils.isNotBlank(csps)) {
                    String cs[] = csps.split(",");
                    List<Privilege> plist = service.query(QueryBuilder.custom(Privilege.class)
                            .andIn("method", cs)
                            .includeFields("id")
                            .build());
                    plist.stream().forEach(e -> dependencies.add(e.getId()));
                }
            }

            for (Long pid : dependencies) {
                MenuPrivilege rp = new MenuPrivilege(menu.getId(), pid);
                rp.setCreator(menu.getEditor());
                rp.setEditor(menu.getEditor());
                service.save(rp);
            }
        }
    }

    public Object queryOneByParentId(String name,boolean isAdd,Long id,boolean isParent){
        QueryBuilder<Menu> custom = QueryBuilder.custom(Menu.class);
        if(isAdd){
            if (name != null && isParent) {
                custom.andEquivalent("name",StringUtils.trim(name));
                custom.andEquivalent("parentId",0);
            }else{
                custom.andEquivalent("name",StringUtils.trim(name));
                custom.andNotEquivalent("parentId",0);
            }

        }else{
            if (name != null && isParent) {
                custom.andEquivalent("name",StringUtils.trim(name));
                custom.andEquivalent("parentId",0);
                custom.andNotEquivalent("id",id);
            }else{
                custom.andEquivalent("name",StringUtils.trim(name));
                custom.andNotEquivalent("parentId",0);
                custom.andNotEquivalent("id",id);
            }
        }
        return service.queryOne(custom.build());
    }
}
