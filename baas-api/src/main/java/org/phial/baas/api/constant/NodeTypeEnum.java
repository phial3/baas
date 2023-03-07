package org.phial.baas.api.constant;

import lombok.Getter;

@Getter
public enum NodeTypeEnum {
    // 每个组织一个CA节点
    CA(0, "ca", "CA节点", ChainTypeEnum.CHAIN_MAKER),

    // ChainMaker NodeType
    CHAIN_MAKER_NODE_CONSENSUS(101, "consensus", "共识节点", ChainTypeEnum.CHAIN_MAKER),
    CHAIN_MAKER_NODE_COMMON(102, "common", "同步节点", ChainTypeEnum.CHAIN_MAKER),
    CHAIN_MAKER_NODE_VM_ENGINE(103, "contract.vm", "合约虚拟机节点", ChainTypeEnum.CHAIN_MAKER),

    // HyperledgerFabric NodeType
    HYPERLEDGER_FABRIC_NODE_PEER(201, "peer", "peer节点", ChainTypeEnum.HYPERLEDGER_FABRIC),
    HYPERLEDGER_FABRIC_NODE_ORDER(202, "orderer", "order节点", ChainTypeEnum.HYPERLEDGER_FABRIC);

    // fisco_bcos NodeType

    // xuperchain NodeType


    private final Integer code;
    private final String type;
    private final String name;
    private final ChainTypeEnum chainType;

    NodeTypeEnum(Integer code, String type, String name, ChainTypeEnum chainType) {
        this.code = code;
        this.type = type;
        this.name = name;
        this.chainType = chainType;
    }

    public NodeTypeEnum getByCode(Integer code) {
        for (NodeTypeEnum nodeType : NodeTypeEnum.values()) {
            if (code.equals(nodeType.getCode())) {
                return nodeType;
            }
        }
        throw new IllegalArgumentException("NodeTypeEnum getByCode code=" + code + " not found");
    }
}
