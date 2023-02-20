package com.phial.baas.api.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysUser extends Entity<Long> {
    private Long id;
    private String user_id; //用户ID，主键
    private String tenant_id; //租户ID
    private String username;//用户名
    private String password; //密码
    private String email; //电子邮箱
    private String phone; //手机号
    private String address; //联系地址

    // 用户类型: 企业; 个人
    private Integer type;

    /**
     * 身份ID
     */
    private String enrollmentId;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 失效时间
     */
    private Date expirationDate;
}
