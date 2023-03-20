package org.phial.baas.service.util.crypto;

import cn.hutool.core.lang.Assert;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.util.Base64Utils;


public class EncodingUtil {

    public static final String STRING = "string";
    public static final String BASE64 = "base64";
    public static final String HEX = "hex";


    public static byte[] decode(String data, String codingMode) {
        Assert.notBlank(codingMode, "codingMode should not be empty");
        byte[] res;
        switch (codingMode.toLowerCase()) {
            case STRING:
                res = data.getBytes();
                break;
            case BASE64:
                res = Base64Utils.decodeFromString(data);
                break;
            case HEX:
                res = Hex.decode(data);
                break;
            default:
                throw new RuntimeException("unknow codingMode=" + codingMode);
        }
        return res;
    }

    public static String encode(byte[] data, String codingMode) {
        Assert.notBlank(codingMode, "codingMode should not be empty");
        String res;
        switch (codingMode) {
            case STRING:
                res = new String(data);
                break;
            case BASE64:
                res = Base64Utils.encodeToString(data);
                break;
            case HEX:
                res = Hex.toHexString(data);
                break;
            default:
                throw new RuntimeException("unknow codingMode=" + codingMode);
        }
        return res;
    }

    public static String base64ToHex(String base64Str) {
        return Hex.toHexString(Base64Utils.decodeFromString(base64Str));
    }
}
