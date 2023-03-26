package org.phial.baas.service.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.phial.baas.service.annootation.EnumValue;
import org.phial.baas.service.annootation.IEnum;

/**
 * @author gyf
 * @date 2022/12/12
 */
public class CryptoEnum {

    @Getter
    public enum CryptoStatus {
        /**
         * 正常
         */
        COMMON("COMMON"),
        FREEZE("FREEZE"),
        REVOKE("REVOKE");


        private final String status;

        CryptoStatus(String status) {
            this.status = status;
        }
    }

    @Getter
    public enum CryptoUsage {
        /**
         * TLS
         */
        TLS("tls", 2),
        SIGN("sign", 1),
        TLS_SIGN("tls-sign", 3),
        CA("ca", 1),
        ROOT("root", 1);


        private final String usage;

        private final Integer keyUsage;

        CryptoUsage(String usage, Integer keyUsage) {
            this.usage = usage;
            this.keyUsage = keyUsage;
        }
    }

    @Getter
    public enum CryptoUserType implements IEnum<Integer> {
        /**
         *
         */
        CLIENT_BUSINESS_USER("client", 8, "business"),
        CLIENT_MEMBER_USER("client", 7, "member"),
        REGISTER("client", 6, "register"),
        ADMIN("client", 5, "admin"),
        PEER("peer", 4, "peer"),
        ORDER("orderer", 3, "orderer"),
        CA("ca", 2, "ca"),
        ROOT("root", 1, "root"),
        ALL("all", 0, "all");


        //表里的
        private final String userType;
        //表里的
        @EnumValue
        private final Integer code;
        //自定义区分business和user
        private final String memberType;

        CryptoUserType(String userType, Integer code,String memberType) {
            this.userType = userType;
            this.code = code;
            this.memberType = memberType;
        }

        @Override
        public Integer getValue() {
            return this.code;
        }

        public static CryptoUserType getCryptoUserByCode(int code) {
            for (CryptoUserType cryptoUserType : CryptoUserType.values()) {
                if (code == cryptoUserType.getCode()) {
                    return cryptoUserType;
                }
            }
            throw new RuntimeException("getCryptoUserType code not exist: " + code);
        }

        public static CryptoUserType getCryptoUserType(String userType) {
            for (CryptoUserType cryptoUserType : CryptoUserType.values()) {
                if (cryptoUserType.getUserType().equals(userType)) {
                    return cryptoUserType;
                }
            }
            throw new RuntimeException("getCryptoUserType userType not exist: " + userType);
        }

        public static CryptoUserType getCryptoUserTypeByMember(String memberType) {
            for (CryptoUserType cryptoUserType : CryptoUserType.values()) {
                if (cryptoUserType.getMemberType().equals(memberType)) {
                    return cryptoUserType;
                }
            }
            throw new RuntimeException("getCryptoUserType memberType not exist: " + memberType);
        }
    }

    @Getter
    @AllArgsConstructor
    public enum CryptoType {
        /**
         *
         */
        CERT(".crt"),
        KEY(".key");

        private final String suffix;
    }
}
