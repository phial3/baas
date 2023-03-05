package org.phial.baas.api.constant;

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
}
