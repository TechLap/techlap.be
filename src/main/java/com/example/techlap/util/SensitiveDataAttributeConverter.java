package com.example.techlap.util;

import jakarta.persistence.AttributeConverter;


import com.example.techlap.service.crypto.CryptoHelper;

import jakarta.persistence.Converter;

@Converter
public class SensitiveDataAttributeConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            CryptoHelper cryptoHelper = SpringContext.getBean(CryptoHelper.class);
            return cryptoHelper.encryptSensitiveData(attribute);
        } catch (Exception e) {
            // Log error and return original data to prevent breaking JPA operations
            return attribute;
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            CryptoHelper cryptoHelper = SpringContext.getBean(CryptoHelper.class);
            return cryptoHelper.decryptSensitiveData(dbData);
        } catch (Exception e) {
            // Log error and return original data to prevent breaking JPA operations
            return dbData;
        }
    }
}