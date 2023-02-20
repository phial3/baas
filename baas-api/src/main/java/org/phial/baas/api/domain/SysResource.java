package org.phial.baas.api.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysResource extends Entity<Long> {
    private String resource_id; //资源ID，主键
    private String tenant_id; //租户ID
    private String resource_name; //资源名称
    private String description; //资源描述
    private String type; //资源类型（菜单、按钮等）
    private String url; //资源URL
    private String icon; //资源图标
    private String parent_id; //父级资源ID
    private String sort; //排序
    private String status; //状态（启用、禁用等）
}
