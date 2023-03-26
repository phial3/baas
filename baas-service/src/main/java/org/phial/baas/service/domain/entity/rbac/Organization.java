package org.phial.baas.service.domain.entity.rbac;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.mayanjun.mybatisx.api.annotation.Column;
import org.mayanjun.mybatisx.api.annotation.Index;
import org.mayanjun.mybatisx.api.annotation.IndexColumn;
import org.mayanjun.mybatisx.api.annotation.Table;
import org.mayanjun.mybatisx.api.enums.DataType;
import org.mayanjun.mybatisx.api.enums.IndexType;
import org.phial.baas.service.domain.entity.NamedEntity;


@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Table(value = "baas_organization",
        indexes = {
                @Index(value = "idx_organization_domain", columns = @IndexColumn("domain"), type = IndexType.UNIQUE),
        },
        comment = "组织信息表"
)
public class Organization extends NamedEntity {

    @Column(comment = "组织域名", type = DataType.VARCHAR, length = "32")
    private String domain;

    @Column(comment = "组织所属国家", type = DataType.VARCHAR, length = "16")
    private String country;

    @Column(comment = "组织所属省份", type = DataType.VARCHAR, length = "16")
    private String province;

    @Column(comment = "组织所属省份区域", type = DataType.VARCHAR, length = "16")
    private String locality;

    @Column(comment = "租户ID", type = DataType.VARCHAR, length = "32")
    private String tenantId;

    /////////////////////////////////////////////
    /////////// 以下property没有持久化 //////////////
    /////////////////////////////////////////////

    public String getRegisterId() {
        return "register." + this.getDomain();
    }

    public String getCaNodeName() {
        return "ca." + this.getDomain();
    }

    public String getMspID() {
        return this.getName() + "MSP";
    }
}
