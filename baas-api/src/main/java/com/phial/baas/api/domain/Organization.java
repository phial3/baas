package com.phial.baas.api.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Organization extends Entity<Long> {
    private String org_id; //组织机构ID，主键
    private String tenant_id; //租户ID
    private String org_name; //组织机构名称
    private String description; //组织机构描述
    private String status; //状态（启用、禁用等）
}
