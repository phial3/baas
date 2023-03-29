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
@Table(value = "t_user_organization",
        indexes = {
                @Index(value = "idx_user", columns = @IndexColumn("user"), type = IndexType.NULL),
        },
        comment = "用户组织关系表"
)
public class UserOrganization extends LongEditableEntity {

    @Column(type = DataType.BIGINT, referenceField = "id")
    private SysUser user;

    @Column(type = DataType.BIGINT, referenceField = "id")
    private Organization organization;

    public UserOrganization() {
    }

    public UserOrganization(Long id) {
        super(id);
    }

    public UserOrganization(SysUser user, Organization org) {
        this.user = user;
        this.organization = org;
    }

    public UserOrganization(Long userId, Long orgId) {
        this.user = new SysUser(userId);
        this.organization = new Organization(orgId);
    }

}
