package org.phial.baas.service.domain.entity.system;

import lombok.Data;
import lombok.EqualsAndHashCode;
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
@Data
@EqualsAndHashCode(callSuper = true)
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
    private String pkgName;

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
}
