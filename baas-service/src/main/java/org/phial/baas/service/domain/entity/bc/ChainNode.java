package org.phial.baas.service.domain.entity.bc;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.mayanjun.mybatisx.api.annotation.Column;
import org.mayanjun.mybatisx.api.annotation.Index;
import org.mayanjun.mybatisx.api.annotation.IndexColumn;
import org.mayanjun.mybatisx.api.annotation.Table;
import org.mayanjun.mybatisx.api.entity.LongEditableEntity;
import org.mayanjun.mybatisx.api.enums.DataType;
import org.mayanjun.mybatisx.api.enums.IndexType;
import org.phial.baas.service.domain.entity.NamedEntity;


/**
 * @author gyf
 * @date 2022/12/12
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Table(value = "baas_chain_node",
        indexes = {
                @Index(value = "idx_chain_id", columns = @IndexColumn("chainId"), type = IndexType.NULL),
        },
        comment = "联盟节点关系表"
)
public class ChainNode extends LongEditableEntity {

    @Column(comment = "链ID", type = DataType.VARCHAR, length = "16")
    private String chainId;

    @Column(comment = "节点ID", type = DataType.BIGINT)
    private Long nodeId;

    @Column(comment = "组织域名", type = DataType.VARCHAR, length = "32")
    private String orgDomain;

    /**
     * 默认构造函数
     */
    public ChainNode() {
    }

    /**
     * id构造函数
     *
     * @param id 数据ID
     */
    public ChainNode(Long id) {
        super(id);
    }
}