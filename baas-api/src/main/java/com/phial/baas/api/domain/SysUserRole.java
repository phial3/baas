package com.phial.baas.api.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserRole extends Entity<Long> {
    private String user_id; //用户ID，主键
    private String role_id; //角色ID，主键;
}
