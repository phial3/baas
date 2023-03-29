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
import org.phial.baas.service.domain.entity.NamedEntity;


@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Table(value = "t_role_organization",
        indexes = {
                @Index(value = "idx_role", columns = @IndexColumn("role"), type = IndexType.NULL),
        },
        comment = "角色组织关系表"
)
public class RoleOrganization extends LongEditableEntity {

    @Column(type = DataType.BIGINT, referenceField = "id")
    private Role role;

    @Column(type = DataType.BIGINT, referenceField = "id")
    private Organization organization;

    public RoleOrganization(){}

    public RoleOrganization(Long id) {
        super(id);
    }

    public RoleOrganization(Role role, Organization org) {
        this.role = role;
        this.organization = org;
    }

    public RoleOrganization(Long roleId, Long orgId) {
        this.role = new Role(roleId);
        this.organization = new Organization(orgId);
    }
}
