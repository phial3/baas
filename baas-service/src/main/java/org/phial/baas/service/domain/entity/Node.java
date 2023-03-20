package org.phial.baas.service.domain.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.phial.baas.service.constant.CommonConstant;
import org.phial.baas.service.constant.NodeStatusEnum;
import org.phial.baas.service.constant.NodeTypeEnum;

import java.io.Serializable;

/**
 * @author gyf
 * @date 2022/12/12
 */
@Data
@Accessors(chain = true)
@TableName("baas_node")
@EqualsAndHashCode(callSuper = true)
public class Node extends Entity<Long> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id",type = IdType.AUTO)
    @Column(comment = "主键id",isAutoIncrement = true)
    private Long id;

    /**
     * 节点名称
     */
    @TableField("name")
    @Column(comment = "节点名称")
    private String name;

    /**
     * 组织域名
     */
    @TableField("org_domain")
    @Column(comment = "组织域名")
    private String orgDomain;

    /**
     * 节点类型
     */
    @TableField("type")
    @Column(comment = "节点类型")
    private NodeTypeEnum type;


    /**
     * 节点状态
     */
    @TableField("status")
    @Column(comment = "节点状态")
    private NodeStatusEnum status;

    /**
     * IP
     */
    @TableField("ip")
    @Column(comment = "节点IP")
    private String ip;

    /**
     * node rpc port
     */
    @TableField("rpc_port")
    @Column(comment = "rpcPort")
    private Long rpcPort;

    /**
     * k8s port
     */
    @TableField("rpc_k8s")
    @Column(comment = "rpcK8s")
    private Long rpcK8s;

    /**
     * p2p port
     */
    @TableField("p2p_port")
    @Column(comment = "p2pPort")
    private Long p2pPort;

    /**
     *
     */
    @TableField("p2p_k8s")
    @Column(comment = "p2pK8s")
    private Long p2pK8s;

    /**
     * monitor port
     */
    @TableField("monitor_port")
    @Column(comment = "monitorPort")
    private Long monitorPort;

    /**
     *
     */
    @TableField("monitor_k8s")
    @Column(comment = "monitorK8s")
    private Long monitorK8s;

    /**
     *
     */
    @TableField("vm_name")
    @Column(comment = "虚拟机名称")
    private String vmName;

    /**
     *
     */
    @TableField("pvc_name")
    @Column(comment = "PVC名称")
    private String pvcName;


    /**
     * CPU 需求
     */
    @TableField("requests_cpu")
    @Column(comment = "requestsCpu")
    private String requestsCpu;

    /**
     * 内存需求
     */
    @TableField("requests_memory")
    @Column(comment = "requestsMemory")
    private String requestsMemory;

    /**
     * CPU限制
     */
    @TableField("limits_cpu")
    @Column(comment = "limitsCpu")
    private String limitsCpu;

    /**
     * 内存限制
     */
    @TableField("limits_memory")
    @Column(comment = "limitsMemory")
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