package org.phial.baas.api.constant;


import lombok.Getter;


@Getter
public enum ChainTypeEnum {
    CHAIN_MAKER(1, "chainmaker"),
    FISCO_BCOS(2, "fisco_bcos"),
    XUPER_CHAIN(3, "xuperchain"),
    HYPERLEDGER_FABRIC(4, "hyperledger_fabric");

    private final Integer code;
    private final String type;

    ChainTypeEnum(Integer code, String type) {
        this.code = code;
        this.type = type;
    }
}


