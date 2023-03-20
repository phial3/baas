package org.phial.baas.chainmaker.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.phial.baas.service.constant.CryptoEnum;

import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Crypto {

    private String nodeName;

    private String nodeId;

    private String cert;

    private Long serialNumber;

    private String privateKey;

    private PublicKey publicKey;

    private String orgDomain;

    private String ski;

    private Date issueDate;

    private Date expirationDate;


    public static String getAppId(String orgDomain) {
        return "admin-" + orgDomain;
    }

    public static String getAppKey(String orgDomain) {
        return orgDomain;
    }

    public static String getFullUserId(String namePrefix, CryptoEnum.CryptoUsage cryptoUsage, String orgDomain) {
        return namePrefix + "." + cryptoUsage.getUsage() + "." + orgDomain;
    }

    public static String getFullUserId(String namePrefix, String orgDomain) {
        return namePrefix + "." + orgDomain;
    }

    public Map<String, String> createTrustRootCertFile(){
        Map<String, String> result = new HashMap<>();
        result.put(orgDomain + "-ca.crt", cert);
        return result;
    }

    public Map<String, String> createNodeCertFile(){
        Map<String, String> result = new HashMap<>();
        result.put(nodeName + ".crt", this.cert);
        result.put(nodeName + ".key", this.privateKey);
        return result;
    }

    public String getNodeCertFileName() {
        return nodeName + CryptoEnum.CryptoType.CERT.getSuffix();
    }

    public String getNodeKeyFileName() {
        return nodeName + CryptoEnum.CryptoType.KEY.getSuffix();
    }


    public boolean isAvailable(){
        return !StringUtils.isEmpty(privateKey) && !StringUtils.isEmpty(cert);
    }

}
