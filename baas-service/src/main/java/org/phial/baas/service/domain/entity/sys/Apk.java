package org.phial.baas.service.domain.entity.sys;

import org.mayanjun.mybatisx.api.annotation.Column;
import org.mayanjun.mybatisx.api.annotation.Index;
import org.mayanjun.mybatisx.api.annotation.IndexColumn;
import org.mayanjun.mybatisx.api.annotation.Table;
import org.mayanjun.mybatisx.api.enums.DataType;
import org.phial.baas.service.domain.entity.NamedEntity;

/**
 * App应用
 * @since 2021/4/8
 * @author mayanjun
 */
@Table(value = "t_apk",
        indexes = {
                @Index(value = "idx_code", columns = @IndexColumn("code"))
        },
        comment = "应用管理")
public class Apk extends NamedEntity {

    /**
     * 应用编码
     */
    @Column(comment = "应用编码", type = DataType.BIGINT)
    private Integer code;
    /**
     * 应用包名
     */
    @Column(comment = "应用包名", type = DataType.VARCHAR, length = "256")
    private String packageName;

    /**
     * 应用路径
     */
    @Column(comment = "应用路径", type = DataType.VARCHAR, length = "256")
    private String file;

    /**
     * 是否强制升级
     */
    @Column(comment = "是否强制升级", type = DataType.BIT, length = "1")
    private Boolean forced;


    /**
     * 默认构造器
     */
    public Apk() {
    }

    /**
     * 默认构造器
     */
    public Apk(Long id) {
        super(id);
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Boolean getForced() {
        return forced;
    }

    public void setForced(Boolean forced) {
        this.forced = forced;
    }
}
