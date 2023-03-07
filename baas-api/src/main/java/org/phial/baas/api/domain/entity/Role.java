package org.phial.baas.api.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Role extends Entity<Long> {
    private Long id; //角色ID，主键
    //private String role_id;
    private String name; //角色名称
    private String tenantId; //租户ID
    private String description; //角色描述
    private Integer status; //状态（1:启用, 0:禁用等）
}
