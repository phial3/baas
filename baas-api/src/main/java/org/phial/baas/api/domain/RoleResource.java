package org.phial.baas.api.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RoleResource extends Entity<Long> {
    private String role_id; //角色ID，主键
    private String resource_id; //资源ID，主键
}
