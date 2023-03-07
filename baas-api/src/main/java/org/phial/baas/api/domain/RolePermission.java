package org.phial.baas.api.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RolePermission extends Entity<Long> {
    private Long id;
    private String roleId; //角色ID，主键
    private String permissionId; //权限ID，主键
}
