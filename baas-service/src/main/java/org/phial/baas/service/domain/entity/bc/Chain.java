package org.phial.baas.service.domain.entity.bc;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.JdbcType;
import org.mayanjun.mybatisx.api.annotation.Column;
import org.mayanjun.mybatisx.api.annotation.Index;
import org.mayanjun.mybatisx.api.annotation.IndexColumn;
import org.mayanjun.mybatisx.api.annotation.Table;
import org.mayanjun.mybatisx.api.enums.DataType;
import org.mayanjun.mybatisx.api.enums.IndexType;
import org.phial.baas.service.constant.ChainStatusEnum;
import org.phial.baas.service.constant.ChainTypeEnum;
import org.phial.baas.service.domain.entity.NamedEntity;


/**
 * @author gyf
 * @date 2022/12/12
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Table(value = "baas_chain",
        indexes = {
                @Index(value = "idx_chain_id", columns = @IndexColumn("chainId"), type = IndexType.UNIQUE),
        },
        comment = "联盟通道表"
)
public class Chain extends NamedEntity {

    @Column(comment = "链的唯一标识ID", type = DataType.VARCHAR, length = "16")
    private String chainId;

    @Column(comment = "联盟链状态", type = DataType.INT)
    private ChainStatusEnum status;

    @Column(comment = "联盟链类型", type = DataType.INT)
    private ChainTypeEnum type;

    @Column(comment = "链当前块高度", type = DataType.BIGINT)
    private Long blockHeight;

    @Column(comment = "链上总交易数", type = DataType.BIGINT)
    private Long txCount;

    @Column(comment = "创世块", type = DataType.BLOB)
    private byte[] genesisBlock;

    /**
     * 默认构造函数
     */
    public Chain() {
    }

    /**
     * id构造函数
     *
     * @param id 数据ID
     */
    public Chain(Long id) {
        super(id);
    }
}