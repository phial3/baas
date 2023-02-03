package org.phial.baas.constant;

public enum NodeStatusEnum {
    INIT(0,"INIT"),
    ONLINE(1,"ONLINE"),
    OFFLINE(2,"OFFLINE");

    private final Integer code;
    private final String status;

    NodeStatusEnum(int code, String status) {
        this.code = code;
        this.status = status;
    }

}
