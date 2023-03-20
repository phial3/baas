package org.phial.baas.service.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ChainStatusEnum {
    /**
     *
     */
    INIT(1, "INIT"),
    ONLINE(2, "ONLINE"),
    OFFLINE(3, "OFFLINE");

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String status;

    ChainStatusEnum(int code, String status) {
        this.code = code;
        this.status = status;
    }

    public ChainStatusEnum getByCode(int code) {
        for (ChainStatusEnum chainStatus : ChainStatusEnum.values()) {
            if (code == chainStatus.getCode()) {
                return chainStatus;
            }
        }
        throw new IllegalArgumentException("ChainStatusEnum getByCode code=" + code + " not found");
    }
}
