//package org.phial.baas.manager.domain.system;
//
//
//import org.mayanjun.mybatisx.api.annotation.Column;
//import org.mayanjun.mybatisx.api.annotation.Index;
//import org.mayanjun.mybatisx.api.annotation.IndexColumn;
//import org.mayanjun.mybatisx.api.annotation.Table;
//import org.mayanjun.mybatisx.api.entity.LongEditableEntity;
//import org.mayanjun.mybatisx.api.enums.DataType;
//
///**
// * 用户角色映射
// * @since 2021/4/8
// * @author mayanjun
// */
//@Table(value = "t_user_role",
//        indexes = {
//                @Index(value = "idx_user", columns = @IndexColumn("user")),
//                @Index(value = "idx_role", columns = @IndexColumn("role"))
//        },
//        comment = "用户角色表")
//public class UserRole extends LongEditableEntity {
//
//    @Column(type = DataType.BIGINT, referenceField = "id")
//    private User user;
//
//    @Column(type = DataType.BIGINT, referenceField = "id")
//    private Role role;
//
//    public UserRole() {
//    }
//
//    public UserRole(Long id) {
//        super(id);
//    }
//
//    public UserRole(User user, Role role) {
//        this.user = user;
//        this.role = role;
//    }
//
//    public UserRole(Long userId, Long roleId) {
//        this.user = new User(userId);
//        this.role = new Role(roleId);
//    }
//
//    public Role getRole() {
//        return role;
//    }
//
//    public void setRole(Role role) {
//        this.role = role;
//    }
//
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
//}
