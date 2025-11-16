package com.retailcorp.retailshopping.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class JsonStringConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute;  // JSON string written directly to JSONB column
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData;     // returned as JSON string
    }
}
