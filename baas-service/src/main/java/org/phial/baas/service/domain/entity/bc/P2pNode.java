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
import org.phial.baas.service.constant.CommonConstant;
import org.phial.baas.service.constant.NodeStatusEnum;
import org.phial.baas.service.constant.NodeTypeEnum;
import org.phial.baas.service.domain.entity.NamedEntity;


/**
 * @author gyf
 * @date 2022/12/12
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Table(value = "baas_node",
        indexes = {
                @Index(value = "idx_org_domain", columns = @IndexColumn("orgDomain"), type = IndexType.NULL),
        },
        comment = "联盟节点信息表"
)
public class P2pNode extends NamedEntity {

    @Column(comment = "组织域名", type = DataType.VARCHAR, length = "32")
    protected String orgDomain;

    @Column(comment = "节点类型", type = DataType.INT)
    protected NodeTypeEnum type;

    @Column(comment = "节点状态", type = DataType.INT)
    protected NodeStatusEnum status;

    @Column(comment = "节点IP", type = DataType.VARCHAR, length = "16")
    protected String ip;

    @Column(comment = "rpcPort", type = DataType.BIGINT)
    protected Long rpcPort;

    @Column(comment = "rpcK8s", type = DataType.BIGINT)
    protected Long rpcK8s;

    @Column(comment = "p2pPort", type = DataType.BIGINT)
    protected Long p2pPort;

    @Column(comment = "p2pK8s", type = DataType.BIGINT)
    protected Long p2pK8s;

    @Column(comment = "monitorPort", type = DataType.BIGINT)
    protected Long monitorPort;

    @Column(comment = "monitorK8s", type = DataType.BIGINT)
    protected Long monitorK8s;

    @Column(comment = "虚拟机名称", type = DataType.VARCHAR, length = "32")
    protected String vmName;

    @Column(comment = "PVC名称", type = DataType.VARCHAR, length = "32")
    protected String pvcName;

    @Column(comment = "requestsCpu", type = DataType.VARCHAR, length = "8")
    protected String requestsCpu;

    @Column(comment = "requestsMemory", type = DataType.VARCHAR, length = "8")
    protected String requestsMemory;

    @Column(comment = "limitsCpu", type = DataType.VARCHAR, length = "8")
    protected String limitsCpu;

    @Column(comment = "limitsMemory", type = DataType.VARCHAR, length = "8")
    protected String limitsMemory;


    /**
     * 默认构造函数
     */
    public P2pNode() {
    }

    /**
     * id构造函数
     *
     * @param id 数据ID
     */
    public P2pNode(Long id) {
        super(id);
    }

    /////////////////////////////////////////////
    /////////// 以下property没有持久化 //////////////
    /////////////////////////////////////////////

    public String getDnsName() {
        return this.getName().replace(".", "-");
    }

    public String getNodeHttpUrl() {
        return CommonConstant.getNodeHttpUrl(this.getRpcK8s());
    }

    public String getNodeGrpcUrl() {
        return CommonConstant.getNodeGrpcUrl(this.getRpcPort());
    }

    public boolean isCaNode() {
        return this.type == NodeTypeEnum.CA;
    }
}