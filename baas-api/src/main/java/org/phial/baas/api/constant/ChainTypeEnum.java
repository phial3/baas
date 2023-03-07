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

    public static ChainTypeEnum getByCode(int code) {
        for (ChainTypeEnum statusEnum : ChainTypeEnum.values()) {
            if (code == statusEnum.getCode()) {
                return statusEnum;
            }
        }
        throw new IllegalArgumentException("ChainTypeEnum getByCode code=" + code + " not found");
    }
}


