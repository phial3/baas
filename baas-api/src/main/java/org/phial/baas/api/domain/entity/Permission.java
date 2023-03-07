package org.phial.baas.api.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Permission extends Entity<Long> {
    private Long id; //  //权限ID，主键
    //private String permission_id;
    private String tenantId; //租户ID
    //private String permission_name; //权限名称
    private String name; //权限名称
    private String description; //权限描述
    private Integer status; //状态（1:启用, 0:禁用等）
}
