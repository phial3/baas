package org.phial.baas.api.constant;

public enum ContractStatusEnum {
    INIT(1, "INIT"),
    NORMAL(2, "NORMAL"),
    REVOKED(3, "REVOKED"),
    FROZEN(4, "FROZEN");

    private final Integer code;
    private final String status;

    ContractStatusEnum(int code, String status) {
        this.status = status;
        this.code = code;
    }
}
