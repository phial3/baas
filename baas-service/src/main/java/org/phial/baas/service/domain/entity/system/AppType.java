package org.phial.baas.service.domain.entity.system;

import lombok.Getter;

/**
 * 应用类型
 * @since 2021/4/8
 * @author mayanjun
 */
@Getter
public enum AppType {

    /**
     * 移动应用
     */
    MOBILE("移动应用", "-"),

    /**
     * WMS云应用
     */
    WMS("WMS云应用", "-"),

    ;


    /**
     * 构造器
     * @param displayName 前端显示名称
     * @param icon 图标
     */
    AppType(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    /**
     * 图标
     */
    private String icon;

    /**
     * 前端显示名称
     */
    private String displayName;


}
