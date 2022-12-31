package com.github.viwluirr.crypt;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Base64;

public class Sm4Example {
    private static final String ALGORITHM = "SM4";
    private static final String KEY_ALGORITHM = "SM4";
    private static final String CIPHER_TRANSFORMATION = "SM4/ECB/PKCS5Padding";
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }
    @Test
    public  void testEncryptDecrypt() throws Exception {
        String data = "Hello, world!";
        byte[] key = "D乌龙茶饮茶".getBytes(StandardCharsets.UTF_8);
        System.out.println("密钥base64:"+Base64.getEncoder().encodeToString(key));

        // 加密
        byte[] encryptedData = encrypt(data.getBytes(StandardCharsets.UTF_8), key);
        String encryptedDataBase64 = Base64.getEncoder().encodeToString(encryptedData);
        System.out.println("加密后的数据（Base64 编码）：" + encryptedDataBase64);

        // 解密
        byte[] decryptedData = decrypt(Base64.getDecoder().decode(encryptedDataBase64), key);
        String decryptedDataStr = new String(decryptedData, StandardCharsets.UTF_8);
        System.out.println("解密后的数据：" + decryptedDataStr);
    }
}
