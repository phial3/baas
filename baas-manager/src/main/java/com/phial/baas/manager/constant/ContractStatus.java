package com.phial.baas.manager.constant;

public enum ContractStatus {
    INIT("INIT", 1),
    NORMAL("NORMAL", 2),
    REVOKED("REVOKED", 3),
    FROZEN("FROZEN", 4);

    private final Integer code;
    private final String status;

    ContractStatus(String status, int code) {
        this.status = status;
        this.code = code;
    }
}
