package org.phial.baas.api.constant;

import lombok.Getter;

@Getter
public enum ChainStatusEnum {
    INIT(1, "INIT"),
    ONLINE(2, "ONLINE"),
    OFFLINE(3, "OFFLINE");

    private final Integer code;
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
