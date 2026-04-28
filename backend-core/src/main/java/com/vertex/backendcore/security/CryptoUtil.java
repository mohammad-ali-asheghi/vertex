package com.vertex.backendcore.security;

import com.vertex.core.config.CommonConstant;
import com.vertex.core.exceptions.ServiceException;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@SuppressWarnings("unused")
public class CryptoUtil {

    public static KeyPair generateRsaKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(CommonConstant.RSA);
        keyGen.initialize(CommonConstant.KEY_GEN_INIT);
        return keyGen.genKeyPair();
    }

    public static PrivateKey getRsaPrivateKey(String base64PrivateKey) {
        try {
            byte[] privateKey = Base64.getDecoder().decode(base64PrivateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
            KeyFactory keyFactory = KeyFactory.getInstance(CommonConstant.RSA);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new ServiceException("get Rsa PrivateKey Has Error:" + e.getMessage());
        }
    }

    public static byte[] decryptWithPrivateRsaKey(byte[] data, String rsaPrivateKeyBase64, String instance) {
        try {
            Cipher cipher = Cipher.getInstance(instance);
            cipher.init(Cipher.DECRYPT_MODE, getRsaPrivateKey(rsaPrivateKeyBase64));
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new ServiceException("decrypt With Private Rsa Key Has Error:" + e.getMessage());
        }
    }
}
