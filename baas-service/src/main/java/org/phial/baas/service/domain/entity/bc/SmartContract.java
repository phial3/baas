package org.phial.baas.service.domain.entity.bc;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.mayanjun.mybatisx.api.annotation.Column;
import org.mayanjun.mybatisx.api.annotation.Index;
import org.mayanjun.mybatisx.api.annotation.IndexColumn;
import org.mayanjun.mybatisx.api.annotation.Table;
import org.mayanjun.mybatisx.api.enums.DataType;
import org.mayanjun.mybatisx.api.enums.IndexType;
import org.phial.baas.service.constant.ContractEnum;
import org.phial.baas.service.domain.entity.NamedEntity;

/**
 * @author gyf
 * @date 2022/12/12
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Table(value = "baas_contract",
        indexes = {
                @Index(value = "idx_chain_id", columns = @IndexColumn("chainId"), type = IndexType.NULL),
        },
        comment = "智能合约信息表"
)
public class SmartContract extends NamedEntity {

    @Column(comment = "所属联盟链", type = DataType.VARCHAR, length = "16")
    private String chainId;

    @Column(comment = "合约版本", type = DataType.INT)
    private Integer version;

    @Column(comment = "合约状态", type = DataType.INT)
    private ContractEnum.ContractStatusEnum status;

    @Column(comment = "合约运行类型", type = DataType.INT)
    private ContractEnum.RuntimeType runtimeType;

    /**
     * 默认构造函数
     */
    public SmartContract() {
    }

    /**
     * id构造函数
     *
     * @param id 数据ID
     */
    public SmartContract(Long id) {
        super(id);
    }
}