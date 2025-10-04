package com.example.techlap.util;

import jakarta.persistence.AttributeConverter;


import com.example.techlap.service.crypto.CryptoHelper;

import jakarta.persistence.Converter;

@Converter
public class SensitiveDataAttributeConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        CryptoHelper cryptoHelper = SpringContext.getBean(CryptoHelper.class);
        return cryptoHelper.encryptSensitiveData(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        CryptoHelper cryptoHelper = SpringContext.getBean(CryptoHelper.class);
        return cryptoHelper.decryptSensitiveData(dbData);
    }
}