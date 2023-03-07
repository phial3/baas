package org.phial.baas.api.constant;

import lombok.Getter;

@Getter
public enum NodeStatusEnum {
    INIT(1, "INIT"),
    ONLINE(2, "ONLINE"),
    OFFLINE(3, "OFFLINE");

    private final Integer code;
    private final String status;

    NodeStatusEnum(int code, String status) {
        this.code = code;
        this.status = status;
    }

    public NodeStatusEnum getByCode(int code) {
        for (NodeStatusEnum statusEnum : NodeStatusEnum.values()) {
            if (code == statusEnum.getCode()) {
                return statusEnum;
            }
        }
        throw new IllegalArgumentException("NodeStatusEnum getByCode code=" + code + " not found");
    }
}
