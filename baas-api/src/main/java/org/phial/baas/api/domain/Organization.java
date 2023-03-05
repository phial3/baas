package org.phial.baas.api.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Organization extends Entity<Long> {
    private Long id; //主键

    private String name;//名称

    /**
     * 域名
     */
    private String domain;
    /**
     * 国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String locality;

    private String org_id; //组织机构ID
    private String tenant_id; //租户ID
    private String org_name; //组织机构名称
    private String description; //组织机构描述
    private String status; //状态（启用、禁用等）


    /////////////////////////////////////////////
    /////////// 以下property没有持久化 //////////////
    /////////////////////////////////////////////

    public String getRegisterId() {
        return "register." + this.domain;
    }

    public String getCaNodeName() {
       return  "ca." + this.domain;
    }

    public String getMspID() {
        return this.name + "MSP";
    }
}
