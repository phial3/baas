package org.phial.baas.manager.config.app;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.mayanjun.util.SecretKeyStore;

@Data
public class AESKey {

    private String key;

    private String iv;

    private SecretKeyStore secretKeyStore;

    public AESKey() {
    }

    public AESKey(String key, String iv) {
        this.key = key;
        this.iv = iv;
    }

    public SecretKeyStore secretKeyStore() {
        if (secretKeyStore == null) {
            this.secretKeyStore = new SecretKeyStore(key, iv);
        }
        return this.secretKeyStore;
    }

    public boolean verify() {
        return StringUtils.isNotBlank(key) && StringUtils.isNotBlank(iv) && key.length() == 32 && iv.length() == 16;
    }

}
