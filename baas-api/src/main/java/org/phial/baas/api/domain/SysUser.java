package org.phial.baas.api.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.phial.baas.api.constant.CryptoEnum;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysUser extends Entity<Long> {
    protected Long id;
    protected String user_id; //用户ID，主键
    protected String tenant_id; //租户ID
    protected String username;//用户名
    protected String password; //密码
    protected String email; //电子邮箱
    protected String phone; //手机号
    protected String address; //联系地址

    // 用户类型: 企业; 个人
    protected CryptoEnum.CryptoUserType userType;

    /**
     * 身份ID
     */
    protected String enrollmentId;

    /**
     * 状态
     */
    protected Integer status;

    /**
     * 失效时间
     */
    protected Date expirationDate;
}
