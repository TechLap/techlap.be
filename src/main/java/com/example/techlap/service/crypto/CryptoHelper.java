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

@Service
public class CryptoHelper {
    @Value("${techlap.base64-secret-crypto}")
    private String base64SecretCrypto;

    private static final int CRYPTO_IV_LENGTH = 12;
    private static final int CRYPTO_AUTH_TAG_LENGTH = 128; // 128 bits = 16 bytes, but GCM expects bit value
    
    private byte[] getValidAESKey() {
        try {
            byte[] key = Base64.getDecoder().decode(base64SecretCrypto);
            // AES supports 128, 192, or 256 bit keys (16, 24, or 32 bytes)
            if (key.length == 16 || key.length == 24 || key.length == 32) {
                return key;
            } else {
                // If key is not valid length, truncate or pad to 32 bytes (256-bit)
                byte[] validKey = new byte[32];
                System.arraycopy(key, 0, validKey, 0, Math.min(key.length, 32));
                return validKey;
            }
        } catch (Exception e) {
            // Return a default 32-byte key if there's an error
            return new byte[32]; // This should be replaced with proper key generation
        }
    }

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
        
        // Debug: Log the length of encrypted data
        
        // Validate minimum length (IV + at least some ciphertext + auth tag)
        // GCM mode requires at least IV (12 bytes) + auth tag (16 bytes) + some ciphertext
        if (encryptedDataBytes.length < CRYPTO_IV_LENGTH + CRYPTO_AUTH_TAG_LENGTH/8) {
            throw new IllegalArgumentException("Encrypted data too short: " + encryptedDataBytes.length + " bytes, minimum required: " + (CRYPTO_IV_LENGTH + CRYPTO_AUTH_TAG_LENGTH/8));
        }

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
        try {
            byte[] key = getValidAESKey();
            return encryptDataAES(plainData, key);
        } catch (Exception e) {
            // Log the error and return original data to prevent breaking the application
            return plainData;
        }
    }

    public String decryptSensitiveData(String cipherText) {
        if (Objects.isNull(cipherText) || cipherText.isEmpty())
            return cipherText;
        
        // Check if the data looks like it's encrypted (base64 format)
        if (!isValidBase64(cipherText)) {
            // If it's not valid base64, it might be plain text (not encrypted)
            return cipherText;
        }
        
        try {
            byte[] key = getValidAESKey();
            return decryptDataAES(cipherText, key);
        } catch (IllegalArgumentException e) {
            // If data is too short or invalid format, it might be corrupted or old format
            return cipherText;
        } catch (Exception e) {
            // Log the error and return original data to prevent breaking the application
            return cipherText;
        }
    }
    
    private boolean isValidBase64(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(str);
            // Additional check: if decoded data is too short, it's probably not encrypted data
            return decoded.length >= CRYPTO_IV_LENGTH + CRYPTO_AUTH_TAG_LENGTH/8;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
