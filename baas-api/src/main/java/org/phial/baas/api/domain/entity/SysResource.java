package org.phial.baas.api.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysResource extends Entity<Long> {
    private Long id;            //资源ID，主键
    //private String resource_id;
    private Long parentId;  //父级资源ID
    private String name; //资源名称
    private String tenantId; //租户ID
    private String description; //资源描述
    private String type; //资源类型（菜单、按钮等）
    private String url; //资源URL
    private String icon; //资源图标
    private String sort; //排序
    private Integer status; //状态（启用、禁用等）
}
