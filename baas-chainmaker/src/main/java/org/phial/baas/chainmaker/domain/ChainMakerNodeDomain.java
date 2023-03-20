package org.phial.baas.chainmaker.domain;


import lombok.Data;
import org.phial.baas.service.constant.CryptoEnum;

import java.util.Map;
@Data
public class ChainMakerNodeDomain extends NodeDomain {

    private DockerVmNodeDomain dockerVmNodeDomain;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }


    public Map<String, String> createNodeCryptoFile() {
        //selectNodeCrypto();
        Crypto crypto = getCrypto();
        return crypto.createNodeCertFile();
    }

    public String getNodeCryptoFileName(CryptoEnum.CryptoType cryptoType) {
        Crypto crypto = getCrypto();
        if (CryptoEnum.CryptoType.CERT.equals(cryptoType)) {
            return crypto.getNodeCertFileName();
        } else {
            return crypto.getNodeKeyFileName();
        }
    }


    public void bindPVC() {

    }

}

