package com.example.client.json;

import jakarta.json.spi.*;
import jakarta.json.stream.*;

public interface JsonpMapper {
    /**
     * Return the JSON-P provider, to create JSON parsers and generators.
     */
    JsonProvider jsonProvider();


    <T> T deserialize(JsonParser parser, Class<T> clazz);


    <T> void serialize(T value, JsonGenerator generator);


    default boolean ignoreUnknownFields() {
        return true;
    }


    default <T> T attribute(String name) {
        return null;
    }


    default <T> T attribute(String name, T defaultValue) {
        T v = attribute(name);
        return v != null ? v : defaultValue;
    }

    <T> JsonpMapper withAttribute(String name, T value);
}