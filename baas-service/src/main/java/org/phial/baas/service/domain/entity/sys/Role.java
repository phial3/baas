//package org.phial.baas.service.domain.entity.sys;
//
//import org.mayanjun.mybatisx.api.annotation.Column;
//import org.mayanjun.mybatisx.api.annotation.Index;
//import org.mayanjun.mybatisx.api.annotation.IndexColumn;
//import org.mayanjun.mybatisx.api.annotation.Table;
//import org.mayanjun.mybatisx.api.entity.LongEditableEntity;
//
///**
// * 角色
// * @since 2021/4/8
// * @author mayanjun
// */
////@Table(value = "t_role",
////        indexes = {
////                @Index(value = "idx_name", columns = @IndexColumn("name"))
////        },
////        comment = "平台角色")
//public class Role extends LongEditableEntity {
//
//    @Column(length = "32")
//    private String name;
//
//    @Column(length = "500")
//    private String description;
//
//    // 接收权限参数
//    private Long privileges[];
//
//    private Long menus[];
//
//    public Long[] getPrivileges() {
//        if (privileges == null) {
//            privileges = new Long[]{};
//        }
//        return privileges;
//    }
//
//    public void setPrivileges(Long[] privileges) {
//        this.privileges = privileges;
//    }
//
//    public Role() {
//    }
//
//    public Role(Long id) {
//        super(id);
//    }
//
//    public Long[] getMenus() {
//        return menus;
//    }
//
//    public void setMenus(Long[] menus) {
//        this.menus = menus;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//}
