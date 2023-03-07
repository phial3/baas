package org.phial.baas.api.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RoleOrganization extends Entity<Long> {
    private Long id;
    private String roleId; //角色ID主键
    private String orgId; //组织机构ID主键
}
