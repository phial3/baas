package org.phial.baas.service.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@TableName("baas_organization")
@EqualsAndHashCode(callSuper = true)
public class Organization extends Entity<Long> implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    @Column(comment = "主键id",isAutoIncrement = true)
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
