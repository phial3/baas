package org.phial.baas.service.domain.entity.sys;

import org.mayanjun.mybatisx.api.annotation.Column;
import org.mayanjun.mybatisx.api.annotation.Index;
import org.mayanjun.mybatisx.api.annotation.IndexColumn;
import org.mayanjun.mybatisx.api.annotation.Table;
import org.mayanjun.mybatisx.api.enums.DataType;
import org.phial.baas.service.domain.entity.NamedEntity;

/**
 * 应用
 * @since 2021/4/8
 * @author mayanjun
 */
@Table(value = "t_app",
        indexes = {
                @Index(value = "idx_name", columns = @IndexColumn("name"))
        },
        comment = "APP KEY")
public class App extends NamedEntity {

    /**
     * 安全码
     */
    @Column(comment = "安全码", type = DataType.VARCHAR, length = "64")
    private String secretKey;

    /**
     * 是否启用
     */
    @Column(comment = "是否启用", type = DataType.BIT, length = "1")
    private Boolean enabled;

    @Column(comment = "应用类型", type = DataType.VARCHAR, length = "32")
    private AppType type;

    /**
     * 默认构造器
     */
    public App() {
    }

    /**
     * 默认构造器
     */
    public App(Long id) {
        super(id);
    }

    /**
     * 获取 secretKey
     *
     * @return secretKey
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * 设置 secretKey
     *
     * @param secretKey secretKey 值
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * 获取 enabled
     *
     * @return enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * 设置 enabled
     *
     * @param enabled enabled 值
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 获取 type
     *
     * @return type
     */
    public AppType getType() {
        return type;
    }

    /**
     * 设置 type
     *
     * @param type type 值
     */
    public void setType(AppType type) {
        this.type = type;
    }
}
