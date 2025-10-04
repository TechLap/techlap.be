package com.example.techlap.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.math.BigDecimal;
import com.example.techlap.service.crypto.CryptoHelper;

@Converter
public class SensitiveBigDecimalAttributeConverter implements AttributeConverter<BigDecimal, String> {

    @Override
    public String convertToDatabaseColumn(BigDecimal attribute) {
        try {
            CryptoHelper cryptoHelper = SpringContext.getBean(CryptoHelper.class);
            if (attribute == null) {
                return null;
            }
            return cryptoHelper.encryptSensitiveData(attribute.toString());
        } catch (Exception e) {
            // Log error and return original data to prevent breaking JPA operations
            System.err.println("Error in SensitiveBigDecimalAttributeConverter.convertToDatabaseColumn: " + e.getMessage());
            return attribute != null ? attribute.toString() : null;
        }
    }

    @Override
    public BigDecimal convertToEntityAttribute(String dbData) {
        try {
            CryptoHelper cryptoHelper = SpringContext.getBean(CryptoHelper.class);
            if (dbData == null || dbData.isEmpty()) {
                return null;
            }
            String decryptedValue = cryptoHelper.decryptSensitiveData(dbData);
            return new BigDecimal(decryptedValue);
        } catch (Exception e) {
            // Log error and return original data to prevent breaking JPA operations
            System.err.println("Error in SensitiveBigDecimalAttributeConverter.convertToEntityAttribute: " + e.getMessage());
            try {
                return new BigDecimal(dbData);
            } catch (NumberFormatException nfe) {
                System.err.println("Failed to parse BigDecimal from: " + dbData);
                return BigDecimal.ZERO;
            }
        }
    }
}
