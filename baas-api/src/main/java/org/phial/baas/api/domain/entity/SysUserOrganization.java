package org.phial.baas.api.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserOrganization extends Entity<Long> {
    private Long id;
    private String userId; //用户ID，主键
    private String orgId; //组织机构ID，主键
}
