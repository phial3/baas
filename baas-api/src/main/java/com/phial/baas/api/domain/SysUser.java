package com.phial.baas.api.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUser extends Entity<Long> {
    private String user_id; //用户ID，主键
    private String tenant_id; //租户ID
    private String username;//用户名
    private String password; //密码
    private String email; //电子邮箱
    private String phone; //手机号码
}
