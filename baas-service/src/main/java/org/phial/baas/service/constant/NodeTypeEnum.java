package org.phial.baas.service.constant;

import lombok.Getter;
import org.phial.baas.service.annootation.EnumValue;
import org.phial.baas.service.annootation.IEnum;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum NodeTypeEnum implements IEnum<Integer> {
    // 每个组织一个CA节点
    CA(0, "ca", "CA节点", ChainTypeEnum.CHAIN_MAKER),

    // ChainMaker NodeType
    CHAIN_MAKER_NODE_CONSENSUS(101, "consensus", "共识节点", ChainTypeEnum.CHAIN_MAKER),
    CHAIN_MAKER_NODE_COMMON(102, "common", "同步节点", ChainTypeEnum.CHAIN_MAKER),
    CHAIN_MAKER_NODE_VM_ENGINE(103, "vm.engine", "虚拟机引擎节点", ChainTypeEnum.CHAIN_MAKER),

    // HyperledgerFabric NodeType
    HYPERLEDGER_FABRIC_NODE_PEER(201, "peer", "peer节点", ChainTypeEnum.HYPERLEDGER_FABRIC),
    HYPERLEDGER_FABRIC_NODE_ORDER(202, "orderer", "order节点", ChainTypeEnum.HYPERLEDGER_FABRIC);

    // fisco_bcos NodeType

    // xuperchain NodeType


    @EnumValue
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

    @Override
    public Integer getValue() {
        return this.code;
    }

    public NodeTypeEnum getByCode(int code) {
        for (NodeTypeEnum nodeType : NodeTypeEnum.values()) {
            if (code == nodeType.getCode()) {
                return nodeType;
            }
        }
        throw new IllegalArgumentException("NodeTypeEnum getByCode code=" + code + " not found");
    }

    public List<NodeTypeEnum> getNodeTypes(ChainTypeEnum chainType) {
        List<NodeTypeEnum> results = new ArrayList<>();
        for (NodeTypeEnum nodeType : NodeTypeEnum.values()) {
            if (nodeType.getChainType() == chainType) {
                results.add(nodeType);
            }
        }
        return results;
    }
}
