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

}
