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
@Table(value = "t_permission",
        indexes = {
                @Index(value = "idx_name", columns = @IndexColumn("name")),
        },
        comment = "权限表"
)
public class Permission extends NamedEntity {

    public Permission() {}

    public Permission(Long id){
        super(id);
    }
}
