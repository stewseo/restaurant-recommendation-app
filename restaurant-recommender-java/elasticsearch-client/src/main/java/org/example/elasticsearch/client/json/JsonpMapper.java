package org.example.elasticsearch.client.json;

import jakarta.json.spi.*;
import jakarta.json.stream.*;
import java.lang.reflect.Type;
import javax.annotation.Nullable;

public interface JsonpMapper {
    /**
     * Return the JSON-P provider, to create JSON parsers and generators.
     */
    JsonProvider jsonProvider();

    /**
     * Deserialize an object, given its class.
     */
    default <T> T deserialize(JsonParser parser, Class<T> clazz) {
        return deserialize(parser, (Type)clazz);
    }

    /**
     * Deserialize an object, given its type.
     */
    <T> T deserialize(JsonParser parser, Type type);

    /**
     * Serialize an object.
     */
    <T> void serialize(T value, JsonGenerator generator);

    /**
     * Should object parsers in the API client be lenient and silently ignore unknown fields?
     *
     * @return {@code true} by default.
     */
    default boolean ignoreUnknownFields() {
        return true;
    }

    /**
     * Get a named attribute associated to this mapper.
     */
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