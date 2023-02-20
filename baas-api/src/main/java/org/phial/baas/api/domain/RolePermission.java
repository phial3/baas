package org.phial.baas.api.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RolePermission extends Entity<Long> {
    private String role_id; //角色ID，主键
    private String permission_id; //权限ID，主键
}
