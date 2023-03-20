package org.phial.baas.service.util.crypto;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.SM4;
import cn.hutool.crypto.symmetric.SymmetricCrypto;


public class SM4Util {

    /**
     * hutool MS4加密
     *
     * @param content 加密内容
     */
    public static String encode(String content, String key) {
        try {
            SymmetricCrypto sm4 = new SM4(Mode.ECB, Padding.NoPadding, key.getBytes());
            // 在NoPadding模式下需要手动补齐分组不为8字节的
            byte[] data = padding(content);
            //加密
            return sm4.encryptHex(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * hutool MS4加密
     *
     * @param content 加密内容
     */
    public static String encode(String content, String key, String codingMode) {

        byte[] keyBytes = EncodingUtil.decode(key, codingMode);
        SymmetricCrypto sm4 = new SM4(Mode.ECB, Padding.NoPadding, keyBytes);
        // 在NoPadding模式下需要手动补齐分组不为8字节的
        byte[] data = padding(content);
        //加密
        return sm4.encryptHex(data);
    }

    /**
     * hutool 解密SM4
     *
     * @param encodeContent
     * @return
     */
    public static String decode(String encodeContent, String key) {
        SymmetricCrypto sm4 = new SM4(Mode.ECB, Padding.NoPadding, key.getBytes());
        String decryptStr = sm4.decryptStr(encodeContent, CharsetUtil.CHARSET_UTF_8);
        //不知道哪冒出来的
        return decryptStr.replaceAll("\u0000", "");
    }

    public static String decode(String encodeContent, String key, String codingMode) {
        byte[] keyBytes = EncodingUtil.decode(key, codingMode);
        SymmetricCrypto sm4 = new SM4(Mode.ECB, Padding.NoPadding, keyBytes);
        String decryptStr = sm4.decryptStr(encodeContent, CharsetUtil.CHARSET_UTF_8);
        //不知道哪冒出来的
        return decryptStr.replaceAll("\u0000", "");
    }

    // 在NoPadding模式下需要手动对齐16字节的倍数
    public static byte[] padding(String arg_text) {
        byte[] encrypt = arg_text.getBytes();
        if (encrypt.length % 16 != 0) { // not a multiple of 8
            // create a new array with a size which is a multiple of 8
            byte[] padded = new byte[encrypt.length + 16 - (encrypt.length % 16)];
            // copy the old array into it
            System.arraycopy(encrypt, 0, padded, 0, encrypt.length);
            encrypt = padded;
        }
        return encrypt;
    }

    public static void main() {


        String decode1 = decode("48314f71c030a64dcb6a8912e255094d", "2X7r7u7Jd84mMFK2");
        System.out.println(decode1);


//        String pk = "-----BEGIN PRIVATE KEY-----\n" +
//                "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgNwZYXsGuNlKtil7o\n" +
//                "sR9mvEt6Xohft/NvIk+/LW1oFJmgCgYIKoEcz1UBgi2hRANCAAQcYi6d/fP60d5z\n" +
//                "MG2ibHGBlZIMq2up+P7WZq/fTdv374i1sodZ9NoZZx8RiyG629B+gJ/GfGpv9rNt\n" +
//                "4uF2mynZ\n" +
//                "-----END PRIVATE KEY-----\n";
//
        String key = "bUkv3nHtPmUANViu";
//
//        String encode = encode(pk, key);
//        System.out.println(encode);


        String decode = decode("43d641e275ea1c2f90a452409722d4cebbba0e8fdca7e031185c2d1f050e3e308292c71a4c76e4c7901162627f22981ecd3e0d66290c8dd121b3869a4590799944366e2a4613a66f48db3aed3eb8636eb6049c0410b75135729f64937040b601efed166218e7225e4d83bb3dca87c0b2f327cb6973179f586d52164b290e17e75ca77a78023c322a7547744370fcda9d5c9ffbcdf960996e58c5110dbfe03f4721f7ee3c3c2829ee8fa37ceb93a43ec548231f74e2d0d42f023ee28bfde917447766ea45e9f33a777de74c93fd341dc17889423e1784fc8a3ca0e5aa65429e1229959a841bc53cbbf98ac6229c065cc999ef94f8fb6b6823afa6fcc81ac610296d23e5fe5eadadfdfa4086647d374281", key);

        System.out.println(decode);
//        System.out.println(decode.equals(pk));
    }
}
