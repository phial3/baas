package org.phial.baas.api.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Role extends Entity<Long> {
    private String role_id; //角色ID，主键
    private String tenant_id; //租户ID
    private String role_name; //角色名称
    private String description; //角色描述
    private String status; //状态（启用、禁用等）;
}
