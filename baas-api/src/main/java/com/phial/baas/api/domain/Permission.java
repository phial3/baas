package com.phial.baas.api.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Permission extends Entity<Long> {
    private String permission_id; //权限ID，主键
    private String tenant_id; //租户ID
    private String permission_name; //权限名称
    private String description; //权限描述
    private String status; //状态（启用、禁用等）
}
