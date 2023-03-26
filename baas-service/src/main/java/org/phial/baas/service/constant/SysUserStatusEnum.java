package org.phial.baas.service.constant;

import lombok.Getter;
import org.phial.baas.service.annootation.EnumValue;
import org.phial.baas.service.annootation.IEnum;

@Getter
public enum SysUserStatusEnum implements IEnum<Integer> {
    /**
     *
     */
    NORMAL(1001, "NORMAL"), //正常
    FREEZE(1002, "FROZEN"), //冻结
    BLOCKED(1003, "BLOCKED"), //封禁
    REVOKED(1004, "REVOKED"); //注销

    @EnumValue
    private final Integer code;
    private final String status;

    SysUserStatusEnum(int code, String status) {
        this.code = code;
        this.status = status;
    }

    @Override
    public Integer getValue() {
        return this.code;
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
