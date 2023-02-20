package org.phial.baas.api.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserResource extends Entity<Long> {
    private String user_id; //用户ID，主键
    private String resource_id; //资源ID，主键
}
