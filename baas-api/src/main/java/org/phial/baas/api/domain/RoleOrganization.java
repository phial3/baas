package org.phial.baas.api.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RoleOrganization extends Entity<Long> {
    private String role_id; //角色ID，主键
    private String org_id; //组织机构ID，主键
}
