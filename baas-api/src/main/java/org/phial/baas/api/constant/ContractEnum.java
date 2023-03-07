package org.phial.baas.api.constant;

import lombok.Getter;

@Getter
public class ContractEnum {

    public enum ContractStatusEnum {
        INIT(1, "INIT"),
        NORMAL(2, "NORMAL"),
        REVOKED(3, "REVOKED"), //销毁
        FROZEN(4, "FROZEN");   //冻结

        private final Integer code;
        private final String status;

        ContractStatusEnum(int code, String status) {
            this.status = status;
            this.code = code;
        }
    }

    public enum RuntimeType {
        RUST("rust", "WASM"),
        CPP("c++", "WXVM"),
        TINY_GO("tinygo", "GASM"),
        SOLIDITY("solidity", "EVM"),
        JAVA("java", "HVM"),
        GOLANG("golang", "GO");

        private final String language;
        private final String runtimeType;

        RuntimeType(String language, String runtimeType) {
            this.language = language;
            this.runtimeType = runtimeType;
        }
    }
}
