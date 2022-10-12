package com.example.client.json;

import jakarta.json.*;
import jakarta.json.spi.*;
import jakarta.json.stream.*;

import java.io.*;
import java.util.*;

@JsonpDeserializable
public interface JsonData extends JsonpSerializable {


    JsonValue toJson();

    /**
     * Converts this object to a JSON node tree.
     */
    JsonValue toJson(JsonpMapper mapper);

    /**
     * Converts this object to a target class. A mapper must have been provided at creation time.
     *
     * @throws IllegalStateException if no mapper was provided at creation time.
     */
    <T> T to(Class<T> clazz);

    /**
     * Converts this object to a target class.
     */
    <T> T to(Class<T> clazz, JsonpMapper mapper);

    /**
     * Converts this object using a deserializer. A mapper must have been provided at creation time.
     *
     * @throws IllegalStateException if no mapper was provided at creation time.
     */
    <T> T deserialize(JsonpDeserializer<T> deserializer);

    /**
     * Converts this object using a deserializer.
     */
    <T> T deserialize(JsonpDeserializer<T> deserializer, JsonpMapper mapper);

    /**
     * Creates a raw JSON value from an existing object. A mapper will be needed to convert the result.
     */
    static <T> JsonData of(T value) {
        if (value instanceof JsonData) {
            return (JsonData) value;
        } else {
            return new JsonDataImpl(value, null);
        }
    }

    /**
     * Creates a raw JSON value from an existing object, along with the mapper to use for further conversions.
     */
    static <T> JsonData of(T value, JsonpMapper mapper) {
        return new JsonDataImpl(value, mapper);
    }

    /**
     * Creates a raw JSON value from a reader.
     * <p>
     * <b>Note:</b> this method is not called {@code from} like {@link #from(Reader)} or {@link #from(InputStream)} to avoid
     * ambiguities with {@link #of(Object)} that will create a JSON string value instead of parsing the JSON text.
     */
    static JsonData fromJson(String json) {
        return from(new StringReader(json));
    }

    /**
     * Creates a raw JSON value from a reader.
     */
    static JsonData from(Reader json) {
        JsonProvider provider = JsonpUtils.provider();
        try(JsonParser parser = provider.createParser(json)) {
            parser.next();
            return new JsonDataImpl(parser.getValue(), null);
        }
    }

    static JsonData from(InputStream json) {
        JsonProvider provider = JsonpUtils.provider();
        try(JsonParser parser = provider.createParser(json)) {
            parser.next();
            return new JsonDataImpl(parser.getValue(), null);
        }
    }

    /**
     * Creates a raw JSON value from a parser. The provider mapper will be used for conversions unless one is
     * explicitly provided using {@link #to(Class, JsonpMapper)}, {@link #toJson(JsonpMapper)} or
     * {@link #deserialize(JsonpDeserializer)}.
     */
    static JsonData from(JsonParser parser, JsonpMapper mapper) {
        parser.next(); // Need to be at the beginning of the value to read
        return of(parser.getValue(), mapper);
    }

    static JsonData from(JsonParser parser, JsonpMapper mapper, JsonParser.Event event) {
        return of(parser.getValue(), mapper);
    }

    JsonpDeserializer<JsonData> _DESERIALIZER = JsonpDeserializer.of(
            EnumSet.allOf(JsonParser.Event.class), ((parser, mapper, event) -> JsonData.from(parser, mapper, event))
    );
}