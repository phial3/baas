package org.phial.baas.manager.service.system;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mayanjun.core.Assert;
import org.mayanjun.core.ServiceException;
import org.mayanjun.mybatisx.api.entity.LongEntity;
import org.mayanjun.mybatisx.api.query.Query;
import org.mayanjun.mybatisx.api.query.QueryBuilder;
import org.phial.baas.manager.config.interceptor.ConsoleSessionManager;
import org.phial.baas.manager.service.ConsoleBaseBusiness;
import org.phial.baas.service.domain.entity.rbac.SysUser;
import org.phial.baas.service.domain.entity.rbac.UserRole;
import org.phial.baas.service.domain.entity.system.UserProfile;
import org.phial.baas.service.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


/**
 * 系统用户管理
 *
 * @author mayanjun
 * @since 2019-07-06
 */
@Component
@DependsOn("ApplicationDataInitializer")
public class UserBusiness extends ConsoleBaseBusiness<SysUser> implements CommandLineRunner {

    private static final String ADMIN_USERNAME = "admin";

    public static final String PW_PATTERN = "^(?![A-Za-z0-9]+$)(?![a-z0-9\\W]+$)(?![A-Za-z\\W]+$)(?![A-Z0-9\\W]+$)[a-zA-Z0-9_\\W]{8,}$";

    private Long rootUserId = null;

    @Autowired
    private ConsoleSessionManager sessionManager;

    @Autowired
    private AttributeBusiness attributeBusiness;

    public UserProfile userProfile() {
        SysUser user = sessionManager.getCurrentUser().getOriginUser();
        user.setPassword(null);

        // 加载用户属性
        Map<String, String> settings = attributeBusiness.allSettings(user.getUsername());
        return new UserProfile(user, settings);
    }

    public String secretKey(Long id) {
        Assert.greatThanZero(id, "用户ID错误");
        SysUser user = service.getInclude(new SysUser(id), "secretKey");
        Assert.notNull(user, "用户不存在");
        return user.getSecretKey();
    }

    private void saveUserRoles(Long uid, Long rids[], SysUser user) {
        if (rids != null && rids.length > 0) {
            for (Long rid : rids) {
                UserRole join = new UserRole(uid, rid);
                join.setCreator(user.getCreator());
                join.setEditor(user.getEditor());
                service.save(join);
            }
        }
    }

    @Override
    protected List<SysUser> doQuery(QueryBuilder<SysUser> builder) {
        builder.excludeFields("password");
        List<SysUser> users = super.doQuery(builder);
        users.forEach(user -> user.setSecretKey(Strings.escape(user.getSecretKey())));
        return users;
    }

    @Override
    protected void doCheck(SysUser entity, boolean update) {

        String username = entity.getUsername();

        Assert.notBlank(username, "用户名不能为空");
        Assert.notNull(entity.getName(), "请输入姓名");
        Assert.notNull(entity.getNumber(), "请输入工号");
        Assert.isTrue(username.matches("[a-zA-Z]{4,}"), "用户名必须由4个以上的英文字母组成,创建后不可更改");
        if (!ADMIN_USERNAME.equalsIgnoreCase(username)) {
            entity.setAdministrator(false);
        }
        Query<SysUser> query=null;
        if (update) {
            entity.setSecretKey(null);
            query = QueryBuilder.custom(SysUser.class)
                    .andEquivalent("number", entity.getNumber())
                    .andNotEquivalent("id",entity.getId())
                    .build();

        } else {
            Assert.notBlank(entity.getPassword(), "密码不能为空");
            entity.setSecretKey(Strings.secretKey(32));
            //根据工号查询是否存在保证工号唯一
            query = QueryBuilder.custom(SysUser.class)
                    .andEquivalent("number", entity.getNumber())
                    .build();
        }
        SysUser mobileUser1 = service.queryOne(query);
        Assert.isNull(mobileUser1,"您所输入的工号已存在");
        if (StringUtils.isNotBlank(entity.getPassword())) {
            Assert.isTrue(entity.getPassword().matches(PW_PATTERN),
                    "密码长度不少于8位，且必须包含数字、大写字母、小写字母、特殊字符");
            String enc = sessionManager.encryptPassword(entity.getPassword());
            entity.setPassword(enc);
        }

        // 验证用户类型
        Assert.notNull(entity.getType(), "用户类型不能为空");
    }

    private void assertEntityExists(Class<? extends LongEntity> cls, Long id, String msg) {
        Assert.greatThanZero(id, msg);
        Query<? extends LongEntity> query = QueryBuilder.custom(cls).andEquivalent("id", id).build();
        LongEntity entity = service.queryOne(query);
        Assert.notNull(entity, msg);
    }

    @Override
    protected long doSave(SysUser bean) {
        return transaction().execute(status -> {
            try {
                Long id = super.doSave(bean);
                saveUserRoles(bean.getId(), bean.getRoles(), bean);
                return id;
            } catch (DuplicateKeyException e) {
                throw new ServiceException("该用户名已经存在");
            }
        });
    }

    @Override
    protected int doUpdate(SysUser bean) {
        return transaction().execute(status -> {
            Query<UserRole> query = QueryBuilder.custom(UserRole.class)
                    .andEquivalent("user", bean.getId())
                    .build();
            service.delete(query);
            saveUserRoles(bean.getId(), bean.getRoles(), bean);
            return super.doUpdate(bean);
        });
    }

    @Override
    protected SysUser doGet(SysUser bean) {
        SysUser user = service.getExclude(bean, "password");
        Query<UserRole> query = QueryBuilder.custom(UserRole.class)
                .andEquivalent("user", bean.getId())
                .build();
        List<UserRole> userRoles = service.query(query);
        if (CollectionUtils.isNotEmpty(userRoles)) {
            Long ids[] = new Long[userRoles.size()];
            for (int i = 0; i < ids.length; i++) {
                ids[i] = userRoles.get(i).getRole().getId();
            }
            user.setRoles(ids);
        }
        return user;
    }

    @Override
    public void delete(Long[] ids) {

        Long cuid = sessionManager.getCurrentUser().getOriginUser().getId();

        if (rootUserId != null) {
            for (long id : ids) {
                if (id == rootUserId) {
                    throw new ServiceException("无法删除系统用户");
                } else if (cuid == id) {
                    throw new ServiceException("请不要自杀式删除");
                }
            }
        }

        // 删除用户的时候应该删除给用户已经分配的权限
        transaction().execute(transactionStatus -> {
            Query<SysUser> deleteQuery = QueryBuilder.custom(getBeanType()).andIn("id", ids).build();
            service.delete(deleteQuery);

            // 删除角色
            Query<UserRole> query = QueryBuilder.custom(UserRole.class).andIn("user", ids).build();
            service.delete(query);
            return true;
        });
    }


    public List<SysUser> all(){
        Query<SysUser> query = QueryBuilder.custom(SysUser.class).andNotEquivalent("username", "admin").build();
        return service.query(query);
    }

    public void updatePassword(SysUser user) {
        Assert.notBlank(user.getPassword(), "请输入密码");
        Assert.isTrue(user.getPassword().matches(PW_PATTERN),
                "新密码长度不少于8位，且必须包含数字、大写字母、小写字母、特殊字符");
        String newPassword = sessionManager.encryptPassword(user.getPassword());
        user.setPassword(newPassword);
        service.update(user);
    }

    //提供给当前登录用户校验旧密码是否正确
    public void equalsPassword(String password){
        SysUser originUser = sessionManager.getCurrentUser().getOriginUser();
        String enc = sessionManager.encryptPassword(password);
        Assert.isTrue(originUser.getPassword().equals(enc),"旧密码不正确");

    }

    @Override
    public void run(String... args) throws Exception {
        Query<SysUser> query = QueryBuilder.custom(SysUser.class)
                .andEquivalent("username", "admin")
                .build();
        SysUser user = service.queryOne(query);
        if (user != null) {
            rootUserId = user.getId();
        }
    }
}