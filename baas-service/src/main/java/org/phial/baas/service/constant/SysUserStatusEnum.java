package org.phial.baas.service.constant;

import lombok.Getter;

@Getter
public enum SysUserStatusEnum {
    NORMAL(1001, "NORMAL"), //正常
    FREEZE(1002, "FROZEN"), //冻结
    BLOCKED(1003, "BLOCKED"), //封禁
    REVOKED(1004, "REVOKED"); //注销

    private final Integer code;
    private final String status;

    SysUserStatusEnum(int code, String status) {
        this.code = code;
        this.status = status;
    }

    public SysUserStatusEnum getByCode(int code) {
        for (SysUserStatusEnum userStatus : SysUserStatusEnum.values()) {
            if (code == userStatus.getCode()) {
                return userStatus;
            }
        }
        throw new IllegalArgumentException("SysUserStatusEnum getByCode code=" + code + " not found");
    }
}
