package org.phial.baas.service.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.phial.baas.service.annootation.EnumValue;
import org.phial.baas.service.annootation.IEnum;


@Getter
public enum ChainTypeEnum implements IEnum<Integer> {
    /**
     *
     */
    CHAIN_MAKER(1, "chainmaker"),
    FISCO_BCOS(2, "fisco_bcos"),
    XUPER_CHAIN(3, "xuperchain"),
    HYPERLEDGER_FABRIC(4, "hyperledger_fabric");

    @EnumValue
    private final Integer code;

    private final String type;

    ChainTypeEnum(Integer code, String type) {
        this.code = code;
        this.type = type;
    }

    @Override
    public Integer getValue() {
        return this.code;
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


