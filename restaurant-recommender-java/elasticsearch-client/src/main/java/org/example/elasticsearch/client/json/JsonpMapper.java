package org.example.elasticsearch.client.json;

import jakarta.json.spi.*;
import jakarta.json.stream.*;


import javax.annotation.*;
import java.lang.reflect.Type;


public interface JsonpMapper {

    JsonProvider jsonProvider();


    default <T> T deserialize(JsonParser parser, Class<T> clazz) {
        return deserialize(parser, (Type)clazz);
    }

    <T> T deserialize(JsonParser parser, Type type);

    <T> void serialize(T value, JsonGenerator generator);


    default boolean ignoreUnknownFields() {
        return true;
    }


    @Nullable
    default <T> T attribute(String name) {
        return null;
    }

    /**
     * Get a named attribute associated to this mapper, with a default value.
     */
    default <T> T attribute(String name, T defaultValue) {
        T v = attribute(name);
        return v != null ? v : defaultValue;
    }

    /**
     * Create a new mapper with an additional attribute.
     * <p>
     * The {@link JsonpMapperFeatures} class contains the names of attributes that all implementations of
     * <code>JsonpMapper</code> must implement.
     *
     * @see JsonpMapperFeatures
     */
    <T> JsonpMapper withAttribute(String name, T value);
}