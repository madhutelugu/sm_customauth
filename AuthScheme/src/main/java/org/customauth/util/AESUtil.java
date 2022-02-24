package org.customauth.util;


import org.customauth.restutils.ForgeRockAPIService;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class AESUtil {

    public static SecretKey getKeyFromPassword(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");
        return secret;
    }


    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static String encrypt(String algorithm, String input, SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }

    public static String decrypt(String algorithm, String cipherText, SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(cipherText));
        return new String(plainText);
    }

    public static void main(String[] args) throws Exception {
        SecretKey key = getKeyFromPassword("madhukumartelugu", ForgeRockAPIService.SALT);
        String cipherText = "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";
        IvParameterSpec ivParameterSpec = AESUtil.generateIv();
        String algorithm = "AES";
        //String cipherText = AESUtil.encrypt(algorithm, input, key);
        System.out.println("Cipher Text: " + cipherText);
        ivParameterSpec = AESUtil.generateIv();
        String plainText = AESUtil.decrypt(algorithm, cipherText, key);
        System.out.println("Plain Text: " + plainText);
    }
}
