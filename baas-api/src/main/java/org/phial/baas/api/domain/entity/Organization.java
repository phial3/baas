package org.phial.baas.api.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Organization extends Entity<Long> {
    private Long id; //主键

    private String name;//组织机构名称

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

    private String tenantId; //租户ID

    private String description; //组织机构描述

    private Integer status; //状态（1:启用, 0:禁用等）


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
