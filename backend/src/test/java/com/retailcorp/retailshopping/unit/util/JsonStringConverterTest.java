package com.retailcorp.retailshopping.unit.util;

import com.retailcorp.retailshopping.util.JsonStringConverter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonStringConverterTest {

    private final JsonStringConverter converter = new JsonStringConverter();

    @Test
    void convertToDatabaseColumn_returnsAttributeUnchanged() {
        String json = "{\"name\": \"John\", \"age\": 30}";
        String result = converter.convertToDatabaseColumn(json);
        assertThat(result).isEqualTo(json);
    }

    @Test
    void convertToDatabaseColumn_handlesNull() {
        String result = converter.convertToDatabaseColumn(null);
        assertThat(result).isNull();
    }

    @Test
    void convertToEntityAttribute_returnsDbDataUnchanged() {
        String json = "{\"name\": \"Jane\", \"age\": 25}";
        String result = converter.convertToEntityAttribute(json);
        assertThat(result).isEqualTo(json);
    }

    @Test
    void convertToEntityAttribute_handlesNull() {
        String result = converter.convertToEntityAttribute(null);
        assertThat(result).isNull();
    }

    @Test
    void convertToDatabaseColumn_handlesEmptyString() {
        String result = converter.convertToDatabaseColumn("");
        assertThat(result).isEmpty();
    }

    @Test
    void convertToEntityAttribute_handlesEmptyString() {
        String result = converter.convertToEntityAttribute("");
        assertThat(result).isEmpty();
    }
}