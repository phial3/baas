package com.phial.baas.manager.constant;


import lombok.Getter;


@Getter
public enum ChainTypeEnum {
    CHAIN_MAKER("chainmaker"),
    FISCO_BCOS("fisco_bcos"),
    XUPER_CHAIN("xuperchain"),
    HYPERLEDGER_FABRIC("hyperledger_fabric");

    private final String chainType;

    ChainTypeEnum(String chainType) {
        this.chainType = chainType;
    }

    @Getter
    private enum ChainMakerNodeType {
        CA("ca", "CA节点"),
        CONSENSUS("consensus", "共识节点"),
        COMMON("common", "同步节点"),
        VM_ENGINE("contract.go", "合约引擎节点");

        private final String type;
        private final String name;

        ChainMakerNodeType(String type, String name) {
            this.type = type;
            this.name = name;
        }

        public ChainMakerNodeType getByType(String type) {
            for (ChainMakerNodeType nodeType : ChainMakerNodeType.values()) {
                if (type.equals(nodeType.getType())) {
                    return nodeType;
                }
            }
            throw new IllegalArgumentException("ChainMakerNodeType getByType type=" + type + " not found");
        }
    }

    @Getter
    private enum FabricNodeType {
        CA("ca", "CA节点"),
        PEER("peer", "peer节点"),
        ORDER("orderer", "order节点");

        private final String type;
        private final String name;

        FabricNodeType(String type, String name) {
            this.type = type;
            this.name = name;
        }

        public FabricNodeType getByType(String type) {
            for (FabricNodeType nodeType : FabricNodeType.values()) {
                if (type.equals(nodeType.getType())) {
                    return nodeType;
                }
            }
            throw new IllegalArgumentException("FabricNodeType getByType type=" + type + " not found");
        }
    }

    @Getter
    private enum BcosNodeType {
        CA("ca", "CA节点");

        private final String type;
        private final String name;

        BcosNodeType(String type, String name) {
            this.type = type;
            this.name = name;
        }

        public BcosNodeType getByType(String type) {
            for (BcosNodeType nodeType : BcosNodeType.values()) {
                if (type.equals(nodeType.getType())) {
                    return nodeType;
                }
            }
            throw new IllegalArgumentException("BcosNodeType getByType type=" + type + " not found");
        }
    }
}


