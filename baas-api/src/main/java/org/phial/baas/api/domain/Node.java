package org.phial.baas.api.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.phial.baas.api.constant.ChainTypeEnum;
import org.phial.baas.api.constant.CommonConstant;
import org.phial.baas.api.constant.CommonFabricConstant;
import org.phial.baas.api.constant.NodeStatusEnum;
import org.phial.baas.api.constant.NodeTypeEnum;

import java.io.Serializable;

/**
 * @author gyf
 * @date 2022/12/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Node extends Entity<Long> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 组织域名
     */
    private String orgDomain;

    /**
     * 节点类型
     */
    private NodeTypeEnum type;

    /**
     * IP
     */
    private String ip;

    /**
     * node rpc port
     */
    private Long rpcPort;

    /**
     * k8s port
     */
    private Long rpcK8s;

    /**
     * p2p port
     */
    private Long p2pPort;

    /**
     *
     */
    private Long p2pK8s;

    /**
     * monitor port
     */
    private Long monitorPort;

    /**
     *
     */
    private Long monitorK8s;

    /**
     *
     */
    private String vmName;

    /**
     *
     */
    private String pvcName;

    /**
     * 节点状态
     */
    private NodeStatusEnum status;


    /**
     * CPU 需求
     */
    private String requestsCpu;

    /**
     * 内存需求
     */
    private String requestsMemory;

    /**
     * CPU限制
     */
    private String limitsCpu;

    /**
     * 内存限制
     */
    private String limitsMemory;


    /////////////////////////////////////////////
    /////////// 以下property没有持久化 //////////////
    /////////////////////////////////////////////

    public String getDnsName() {
        return this.name.replace(".", "-");
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