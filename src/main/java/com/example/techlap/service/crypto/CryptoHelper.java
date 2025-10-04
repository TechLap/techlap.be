package com.example.techlap.service.crypto;

import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.Cipher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.vavr.control.Try;

@Service
public class CryptoHelper {
    @Value("${techlap.base64-secret-crypto}")
    private String base64SecretCrypto;

    private static final int CRYPTO_IV_LENGTH = 12;
    private static final int CRYPTO_AUTH_TAG_LENGTH = 16;

    public String encryptDataAES(String plainData, byte[] key) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

        byte[] iv = new byte[CRYPTO_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(CRYPTO_AUTH_TAG_LENGTH, iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec);
        byte[] cipherText = cipher.doFinal(plainData.getBytes());

        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
        byteBuffer.put(iv);
        byteBuffer.put(cipherText);

        byte[] cipherMessage = byteBuffer.array();
        return Base64.getEncoder().encodeToString(cipherMessage);
    }

    public static String decryptDataAES(String encryptedDataInBase64, byte[] key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        byte[] encryptedDataBytes = Base64.getDecoder().decode(encryptedDataInBase64.getBytes());

        // Remember we stored the IV as the first 12 bytes while encrypting?
        byte[] iv = Arrays.copyOfRange(encryptedDataBytes, 0, CRYPTO_IV_LENGTH);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(CRYPTO_AUTH_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

        // Use everything from 12 bytes on as ciphertext
        byte[] cipherBytes = Arrays.copyOfRange(encryptedDataBytes, CRYPTO_IV_LENGTH, encryptedDataBytes.length);
        byte[] plainText = cipher.doFinal(cipherBytes);

        return new String(plainText);
    }

    public String encryptSensitiveData(String plainData) {
        if (Objects.isNull(plainData) || plainData.isEmpty())
            return plainData;
        return Try.of(() -> encryptDataAES(plainData, base64SecretCrypto.getBytes()))
                .get();
    }

    public String decryptSensitiveData(String cipherText) {
        if (Objects.isNull(cipherText) || cipherText.isEmpty())
            return cipherText;
        return Try.of(() -> decryptDataAES(cipherText, base64SecretCrypto.getBytes()))
                .get();
    }

}
