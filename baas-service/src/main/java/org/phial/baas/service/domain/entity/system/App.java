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
 * 应用
 * @since 2021/4/8
 * @author mayanjun
 */
@Data
@EqualsAndHashCode(callSuper = true)
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
}
