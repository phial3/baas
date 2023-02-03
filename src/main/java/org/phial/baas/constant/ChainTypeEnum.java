package org.phial.baas.constant;

import lombok.Data;
import lombok.Getter;

interface NodeType {
    String getType();

    String getName();
}

@Data
public class ChainTypeEnum {


    @Data
    public final static class ChainMaker {

    }

    @Getter
    public enum ChainMakerNodeType implements NodeType {
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

        @Override
        public String getType() {
            return type;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    @Getter
    public enum FabricNodeType implements NodeType {
        CA("ca", "CA节点"),
        PEER("peer", "peer节点"),
        ORDER("orderer", "order节点");

        private final String type;
        private final String name;

        FabricNodeType(String type, String name) {
            this.type = type;
            this.name = name;
        }

        @Override
        public String getType() {
            return type;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    @Getter
    public enum BcosNodeType implements NodeType {
        CA("ca", "CA节点");

        private final String type;
        private final String name;

        BcosNodeType(String type, String name) {
            this.type = type;
            this.name = name;
        }

        @Override
        public String getType() {
            return type;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}


