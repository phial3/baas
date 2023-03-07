package org.phial.baas.api.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RoleResource extends Entity<Long> {
    private Long id;
    private String roleId; //角色ID，主键
    private String resourceId; //资源ID，主键
}
