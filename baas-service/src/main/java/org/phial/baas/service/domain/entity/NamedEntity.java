package org.phial.baas.service.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mayanjun.mybatisx.api.annotation.Column;
import org.mayanjun.mybatisx.api.entity.LongEditableEntity;
import org.mayanjun.mybatisx.api.enums.DataType;

/**
 * 命名可编辑实体
 *
 * @author mayanjun
 * @since 2021/4/8
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NamedEntity extends LongEditableEntity {

    public NamedEntity() {
    }

    public NamedEntity(Long id) {
        super(id);
    }

    public NamedEntity(Long id, String name) {
        super(id);
        this.name = name;
    }

    @Column(comment = "名称", type = DataType.VARCHAR, length = "64")
    protected String name;

    @Column(comment = "名称拼音", type = DataType.VARCHAR, length = "255")
    protected String pinyin;

    @Column(comment = "扩展信息", type = DataType.VARCHAR, length = "500")
    protected String extras;

    @Column(comment = "备注", type = DataType.VARCHAR, length = "500")
    protected String description;

    @Column(comment = "是否启用", type = DataType.BIT, length = "1", defaultValue = "1")
    protected Boolean enabled;
}
