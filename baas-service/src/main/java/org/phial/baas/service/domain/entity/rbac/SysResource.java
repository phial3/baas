package org.phial.baas.service.domain.entity.rbac;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.mayanjun.mybatisx.api.annotation.Column;
import org.mayanjun.mybatisx.api.annotation.Table;
import org.mayanjun.mybatisx.api.enums.DataType;
import org.phial.baas.service.domain.entity.NamedEntity;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Table(value = "baas_resource",
        indexes = {},
        comment = "系统资源表"
)
public class SysResource extends NamedEntity {
    @Column(comment = "父级资源ID", type = DataType.BIGINT)
    private Long parentId;

    @Column(comment = "租户ID", type = DataType.VARCHAR, length = "32")
    private String tenantId;

    @Column(comment = "资源类型（菜单、按钮等）", type = DataType.VARCHAR, length = "32")
    private String type;

    @Column(comment = "资源URL", type = DataType.VARCHAR, length = "32")
    private String url;

    @Column(comment = "资源图标", type = DataType.VARCHAR, length = "32")
    private String icon;

    @Column(comment = "排序", type = DataType.VARCHAR, length = "32")
    private String sort;
}
