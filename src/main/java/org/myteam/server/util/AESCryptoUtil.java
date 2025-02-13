package org.myteam.server.util;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class AESCryptoUtil {

    private static final String ALGORITHM = "AES/CBC/PKCS5PADDING";
    private static final String SECRET_KEY_ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;

    @Value("${playhive.control.aesSecretKey}") private String secretKey;
    @Value("${playhive.control.aesIv}") private String iv;

    public String createEncodedPwd(String password) {
        SecretKey secretKey = getSecretKeyFromString(this.secretKey);
        IvParameterSpec iv = getIvFromString(this.iv);

        return encrypt(password, secretKey, iv);
    }

    public String findOriginPwd(String encodedPassword) {
        SecretKey secretKey = getSecretKeyFromString(this.secretKey);
        IvParameterSpec iv = getIvFromString(this.iv);

        return decrypt(encodedPassword, secretKey, iv);
    }

    public String encrypt(String plainText, SecretKey secretKey, IvParameterSpec iv) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new PlayHiveException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public String decrypt(String encryptedText, SecretKey secretKey, IvParameterSpec iv) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new PlayHiveException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public SecretKey getSecretKeyFromString(String secretKey) {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, SECRET_KEY_ALGORITHM);
    }

    public IvParameterSpec getIvFromString(String iv) {
        byte[] decodedIv = Base64.getDecoder().decode(iv);
        return new IvParameterSpec(decodedIv);
    }
}
