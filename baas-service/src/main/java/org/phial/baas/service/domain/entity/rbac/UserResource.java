package org.phial.baas.service.domain.entity.rbac;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.mayanjun.mybatisx.api.annotation.Column;
import org.mayanjun.mybatisx.api.annotation.Index;
import org.mayanjun.mybatisx.api.annotation.IndexColumn;
import org.mayanjun.mybatisx.api.annotation.Table;
import org.mayanjun.mybatisx.api.entity.LongEditableEntity;
import org.mayanjun.mybatisx.api.enums.DataType;
import org.mayanjun.mybatisx.api.enums.IndexType;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Table(value = "t_user_resource",
        indexes = {
                @Index(value = "idx_user", columns = @IndexColumn("user"), type = IndexType.NULL),
        },
        comment = "用户资源关系表"
)
public class UserResource extends LongEditableEntity {

    @Column(type = DataType.BIGINT, referenceField = "id")
    private SysUser user;

    @Column(type = DataType.BIGINT, referenceField = "id")
    private SysResource resource;


    public UserResource() {
    }

    public UserResource(Long id) {
        super(id);
    }

    public UserResource(SysUser user, SysResource resource) {
        this.user = user;
        this.resource = resource;
    }

    public UserResource(Long userId, Long resourceId) {
        this.user = new SysUser(userId);
        this.resource = new SysResource(resourceId);
    }
}
