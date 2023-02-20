package org.phial.baas.manager.util.crypto;

import cn.hutool.core.lang.Assert;
import jodd.util.Base64;
import org.bouncycastle.pqc.legacy.math.linearalgebra.ByteUtils;
import org.bouncycastle.util.encoders.Hex;

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
                res = Base64.decode(data);
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
                res = Base64.encodeToString(data);
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
        return ByteUtils.toHexString(org.bouncycastle.util.encoders.Base64.decode(base64Str));
    }
}
