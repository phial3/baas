package org.phial.baas.service.domain.entity.sys;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.mayanjun.mybatisx.api.entity.LongEntity;
import org.phial.baas.service.domain.entity.rbac.Organization;

/**
 * 用户类型
 *
 * @author mayanjun
 * @since 2021/4/8
 */
public enum UserType {

    /**
     * 机构账号
     */
    ORG("机构账号", Organization.class, "-"),

    ;


    /**
     * 构造器
     *
     * @param displayName 前端显示名称
     * @param icon        图标
     */
    UserType(String displayName, Class<? extends LongEntity> entityType, String icon) {
        this.displayName = displayName;
        this.icon = icon;
        this.entityType = entityType;
    }

    /**
     * 图标
     */
    private String icon;

    /**
     * 前端显示名称
     */
    private String displayName;

    private Class<? extends LongEntity> entityType;

    /**
     * 获取 displayName
     *
     * @return displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 获取 icon
     *
     * @return icon
     */
    public String getIcon() {
        return icon;
    }


    /**
     * 获取 entityType
     *
     * @return entityType
     */
    @JsonIgnore
    public Class<? extends LongEntity> getEntityType() {
        return entityType;
    }
}
