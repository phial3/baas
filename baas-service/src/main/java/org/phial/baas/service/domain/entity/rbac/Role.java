package org.phial.baas.service.domain.entity.rbac;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.mayanjun.mybatisx.api.annotation.Column;
import org.mayanjun.mybatisx.api.annotation.Index;
import org.mayanjun.mybatisx.api.annotation.IndexColumn;
import org.mayanjun.mybatisx.api.annotation.Table;
import org.mayanjun.mybatisx.api.enums.DataType;
import org.phial.baas.service.domain.entity.NamedEntity;


@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Table(value = "t_role",
        indexes = {
                @Index(value = "idx_name", columns = @IndexColumn("name"))
        },
        comment = "角色表"
)
public class Role extends NamedEntity {

    public Role(){}

    public Role(Long id) {
        super(id);
    }

    //接收权限参数
    private Long[] privileges;
    private Long[] menus;
}
