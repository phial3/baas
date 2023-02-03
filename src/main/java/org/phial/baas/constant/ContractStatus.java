package org.phial.baas.constant;

public enum ContractStatus {
    INIT("INIT"),
    NORMAL("NORMAL"),
    REVOKED("REVOKED"),
    FROZEN("FROZEN");

    private final String status;

    ContractStatus(String status) {
        this.status = status;
    }
}
