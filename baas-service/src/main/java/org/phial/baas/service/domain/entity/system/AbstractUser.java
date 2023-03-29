package org.phial.baas.service.domain.entity.system;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mayanjun.mybatisx.api.annotation.Column;
import org.mayanjun.mybatisx.api.enums.DataType;
import org.phial.baas.service.constant.CryptoEnum;
import org.phial.baas.service.constant.SysUserStatusEnum;
import org.phial.baas.service.domain.entity.NamedEntity;

import java.util.Date;

/**
 * 用户抽象
 *
 * @author mayanjun
 * @since 2021/4/8
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractUser extends NamedEntity {

    public AbstractUser() {
    }

    public AbstractUser(Long id) {
        super(id);
    }

    public AbstractUser(String username) {
        this.username = username;
        this.loginTime = System.currentTimeMillis();
    }

    public AbstractUser(String username, long loginTime) {
        this.username = username;
        this.loginTime = loginTime;
    }

    public AbstractUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Column(comment = "用户名", type = DataType.VARCHAR, length = "32")
    private String username;

    @Column(comment = "工号", type = DataType.VARCHAR, length = "32")
    private String number;

    @Column(comment = "密码", type = DataType.VARCHAR, length = "255")
    private String password;

    @Column(comment = "接口调用安全码", type = DataType.VARCHAR, length = "64")
    private String secretKey;

    @Column(comment = "身份ID", type = DataType.VARCHAR, length = "32")
    protected String enrollmentId;

    @Column(comment = "租户ID", type = DataType.VARCHAR, length = "32")
    protected String tenantId;

    @Column(comment = "电子邮箱", type = DataType.VARCHAR, length = "32")
    protected String email;

    @Column(comment = "手机号", type = DataType.VARCHAR, length = "16")
    protected String phone;

    @Column(comment = "联系地址", type = DataType.VARCHAR, length = "255")
    protected String address;

    @Column(comment = "账号类型:企业;个人", type = DataType.INT)
    protected CryptoEnum.CryptoUserType type;

    @Column(comment = "用户账号状态", type = DataType.INT)
    protected SysUserStatusEnum status;

    @Column(comment = "证书失效时间", type = DataType.DATETIME)
    protected Date expirationDate;

    /**
     * 登录成功后颁发的令牌
     */
    protected String token;

    protected long loginTime;

}
