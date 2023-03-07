package org.phial.baas.api.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserRole extends Entity<Long> {
    private Long id;
    private String userId; //用户ID，主键
    private String roleId; //角色ID，主键;
}
