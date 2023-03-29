package org.phial.baas.service.domain.entity.system;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mayanjun.mybatisx.api.annotation.Column;
import org.mayanjun.mybatisx.api.annotation.Index;
import org.mayanjun.mybatisx.api.annotation.IndexColumn;
import org.mayanjun.mybatisx.api.annotation.Table;
import org.mayanjun.mybatisx.api.entity.LongEditableEntity;
import org.mayanjun.mybatisx.api.enums.DataType;

import java.util.List;

/**
 * 地区
 * @since 2021/4/8
 * @author mayanjun
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "t_region",
        indexes = {
                @Index(value = "idx_name", columns = @IndexColumn("name")),
                @Index(value = "idx_pinyin", columns = @IndexColumn("pinyin"))
        },
        comment = "地区")
public class Region extends LongEditableEntity {

    @Column(length = "32")
    private String name;

    @Column(type = DataType.BIGINT, referenceField = "id")
    private Region parent;

    @Column(length = "32")
    private String shortName;

    @Column(type = DataType.INT)
    private Integer level;

    @Column(length = "10")
    private String number;

    @Column(length = "10")
    private String postcode;

    @Column(length = "255")
    private String longName;

    @Column(length = "10,7", type = DataType.DOUBLE)
    private Double longitude;

    @Column(length = "10,7", type = DataType.DOUBLE)
    private Double latitude;

    @Column(length = "500")
    private String pinyin;

    private List<Region> children;

    public Region() {
    }

    public Region(Long id) {
        super(id);
    }
}
