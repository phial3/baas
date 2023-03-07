package org.phial.baas.api.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RolePermission extends Entity<Long> {
    private Long id;
    private String roleId; //角色ID，主键
    private String permissionId; //权限ID，主键
}
