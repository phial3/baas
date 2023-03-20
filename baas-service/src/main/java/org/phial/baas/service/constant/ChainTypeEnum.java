package org.phial.baas.service.constant;


import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;


@Getter
public enum ChainTypeEnum {
    /**
     *
     */
    CHAIN_MAKER(1, "chainmaker"),
    FISCO_BCOS(2, "fisco_bcos"),
    XUPER_CHAIN(3, "xuperchain"),
    HYPERLEDGER_FABRIC(4, "hyperledger_fabric");

    @EnumValue
    private final Integer code;
    @JsonValue
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


