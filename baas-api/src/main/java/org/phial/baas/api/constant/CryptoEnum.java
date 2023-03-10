package org.phial.baas.api.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author gyf
 * @date 2022/12/12
 */
public class CryptoEnum {

    @Getter
    public enum CryptoStatus {
        COMMON("COMMON"),
        FREEZE("FREEZE"),
        REVOKE("REVOKE");


        private final String status;

        CryptoStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }
    }

    @Getter
    public enum CryptoUsage {
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
    @AllArgsConstructor
    public enum CryptoUserType {
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
        private final Integer code;
        //自定义区分business和user
        private final String memberType;


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
        CERT(".crt"),
        KEY(".key");

        private final String suf;
    }
}
