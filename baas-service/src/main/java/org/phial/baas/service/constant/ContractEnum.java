package org.phial.baas.service.constant;

import lombok.Getter;
import org.phial.baas.service.annootation.EnumValue;
import org.phial.baas.service.annootation.IEnum;


public class ContractEnum {

    @Getter
    public enum ContractStatusEnum implements IEnum<Integer> {
        /**
         *
         */
        INIT(1, "INIT"),
        NORMAL(2, "NORMAL"),
        REVOKED(3, "REVOKED"), //销毁
        FROZEN(4, "FROZEN");   //冻结

        @EnumValue
        private final Integer code;
        private final String status;

        ContractStatusEnum(int code, String status) {
            this.status = status;
            this.code = code;
        }

        @Override
        public Integer getValue() {
            return this.code;
        }

        public static ContractStatusEnum getByCode(int code) {
            for (ContractStatusEnum contractStatus : ContractStatusEnum.values()) {
                if (code == contractStatus.getCode()) {
                    return contractStatus;
                }
            }
            throw new IllegalArgumentException("ContractStatusEnum getByCode code=" + code + " not found");
        }
    }

    @Getter
    public enum RuntimeType implements IEnum<Integer>{
        /**
         *
         */
        RUST(1,"rust", "WASM"),
        CPP(2,"c++", "WXVM"),
        TINY_GO(3,"tinygo", "GASM"),
        SOLIDITY(4,"solidity", "EVM"),
        JAVA(5,"java", "HVM"),
        GOLANG(6,"golang", "GO");

        @EnumValue
        private final Integer code;
        private final String language;
        private final String runtimeType;

        RuntimeType(Integer code, String language, String runtimeType) {
            this.code = code;
            this.language = language;
            this.runtimeType = runtimeType;
        }

        @Override
        public Integer getValue() {
            return this.code;
        }

        public static RuntimeType getByCode(int code) {
            for (RuntimeType runtime : RuntimeType.values()) {
                if (code == runtime.getCode()) {
                    return runtime;
                }
            }
            throw new IllegalArgumentException("ContractEnum.RuntimeType getByCode code=" + code + " not found");
        }
    }
}
