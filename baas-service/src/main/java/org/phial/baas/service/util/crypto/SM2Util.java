package org.phial.baas.service.util.crypto;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @ClassName SM2Util
 **/
@Slf4j
public class SM2Util {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * SM2加密算法
     * @param publicKeyStr     公钥
     * @param data          明文数据
     * @return
     */
    public static String encrypt(String publicKeyStr, String data){
        PublicKey publicKey = getPublicKey(publicKeyStr);
        //SM2开始加密数据
        ECPublicKeyParameters ecPublicKeyParameters = null;
        if (publicKey instanceof BCECPublicKey) {
            BCECPublicKey bcecPublicKey = (BCECPublicKey) publicKey;
            ECParameterSpec ecParameterSpec = bcecPublicKey.getParameters();
            ECDomainParameters ecDomainParameters = new ECDomainParameters(
                    ecParameterSpec.getCurve(),
                    ecParameterSpec.getG(),
                    ecParameterSpec.getN());
            ecPublicKeyParameters = new ECPublicKeyParameters(bcecPublicKey.getQ(), ecDomainParameters);
        }
        SM2Engine sm2Engine = new SM2Engine();
        sm2Engine.init(true, new ParametersWithRandom(ecPublicKeyParameters, new SecureRandom()));
        byte[] arrayOfBytes = null;
        try {
            byte[] in = data.getBytes(StandardCharsets.UTF_8);
            arrayOfBytes = sm2Engine.processBlock(in,0, in.length);
        } catch (Exception e) {
            log.error("SM2加密时出现异常:{}", e.getMessage(), e);
        }
        return  Base64.encodeBase64String(arrayOfBytes);
    }

    /**
     * SM2解密算法
     * @param privateKeyStr        私钥
     * @param cipherData        密文数据
     * @return
     */
    public static String decrypt(String privateKeyStr, String cipherData){
        PrivateKey privateKey = getPrivateKey(privateKeyStr);
        BCECPrivateKey bcecPrivateKey = (BCECPrivateKey) privateKey;
        ECParameterSpec ecParameterSpec = bcecPrivateKey.getParameters();
        ECDomainParameters ecDomainParameters = new ECDomainParameters(
                ecParameterSpec.getCurve(),
                ecParameterSpec.getG(),
                ecParameterSpec.getN());
        ECPrivateKeyParameters ecPrivateKeyParameters = new ECPrivateKeyParameters(bcecPrivateKey.getD(),
                ecDomainParameters);
        SM2Engine sm2Engine = new SM2Engine();
        sm2Engine.init(false, ecPrivateKeyParameters);
        String result = null;

        byte[] arrayOfBytes = null;
        try {
            byte[] in = Base64.decodeBase64(cipherData);
            arrayOfBytes = sm2Engine.processBlock(in,0, in.length);
            result = new String(arrayOfBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("SM2解密时出现异常:{}", e.getMessage(), e);
        }
        return result;
    }


    /**
     * 私钥签名
     */
    public static String signByPrivateKey(String data, String privateKeyStr) throws Exception {
        PrivateKey privateKey = getPrivateKey(privateKeyStr);
        Signature sig = Signature.getInstance(GMObjectIdentifiers.sm2sign_with_sm3.toString(), BouncyCastleProvider.PROVIDER_NAME);
        sig.initSign(privateKey);
        sig.update(data.getBytes());
        return Base64.encodeBase64String(sig.sign());
    }

    /**
     * 公钥验签
     */
    public static boolean verifyByPublicKey(String data, PublicKey publicKey, String signature) throws Exception {
        Signature sig = Signature.getInstance(GMObjectIdentifiers.sm2sign_with_sm3.toString(), BouncyCastleProvider.PROVIDER_NAME);
        sig.initVerify(publicKey);
        sig.update(data.getBytes());
        return sig.verify(Base64.decodeBase64(signature));
    }

    public static PrivateKey getPrivateKey(String privateKeyStr) {
        PrivateKey privateKey;
        byte[] keyBytes;
        try {
            keyBytes = Base64.decodeBase64(privateKeyStr.replaceAll("-----BEGIN PRIVATE KEY-----", "").replaceAll("-----END PRIVATE KEY-----", ""));
            KeyFactory keyFactory = KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            log.error("SM2字符串私钥转换异常:{}", e.getMessage(), e);
            throw new RuntimeException("SM2字符串私钥转换异常:" + e.getMessage(), e);
        }
        return privateKey;
    }

    public static PublicKey getPublicKey(String publicKeyStr) {
        PublicKey publicKey;
        try {
            //开始转换字符串公钥 公钥值：publicKeyStr 数据值:data
            byte[] keyBytes = Base64.decodeBase64(publicKeyStr.replaceAll("-----BEGIN PUBLIC KEY-----", "").replaceAll("-----END PUBLIC KEY-----", ""));
            KeyFactory keyFactory = KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            publicKey = keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            log.error("SM2字符串公钥转换异常:{}", e.getMessage(), e);
            throw new RuntimeException("SM2字符串私钥转换异常:" + e.getMessage(), e);
        }
        return publicKey;
    }


    public static void main() throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        String pub =
                "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEp3jwYjtk+pFMhZLBJpYCXbS8zWkOu/jKaQZ97r5vOaNPF1/VYGN6HEBXmRj8UHJdcW4Wv8fxF0SUm3UUCxLCHg==";

        String publicKey = "-----BEGIN PUBLIC KEY-----\n" +
                "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEbdlrP+r1P/77KOkaHtcwlii4249k\n" +
                "c6QN1QzMsZQgPazuty4N4W4THVXj15iePo4lyYa8G/IFRLK+4WXY7a4xWg==\n" +
                "-----END PUBLIC KEY-----\n";

        String pri =
                "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQg6WO8o1ScG5kQmEDy\n" +
                "sp1Qj5zyo0gEmYraFu0WL2UUG1mgCgYIKoEcz1UBgi2hRANCAASnePBiO2T6kUyF\n" +
                "ksEmlgJdtLzNaQ67+MppBn3uvm85o08XX9VgY3ocQFeZGPxQcl1xbha/x/EXRJSb\n" +
                "dRQLEsIe";


        String a = "12345";

        PublicKey pk = getPublicKey(publicKey);
        String s = Base64.encodeBase64String(pk.getEncoded());

        String bytes = signByPrivateKey(a, pri);


//        boolean b = verifyByPublicKey(a, pub, bytes);

//        System.out.println(b);
    }

}

