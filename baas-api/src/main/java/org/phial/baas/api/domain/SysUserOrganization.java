package org.phial.baas.api.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserOrganization extends Entity<Long> {
    private String user_id; //用户ID，主键
    private String org_id; //组织机构ID，主键
}
