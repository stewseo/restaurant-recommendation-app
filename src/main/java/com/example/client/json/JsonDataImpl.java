package com.example.client.json;

import jakarta.json.*;
import jakarta.json.stream.*;

import java.io.*;

class JsonDataImpl implements JsonData {
    private final Object value;
    private final JsonpMapper mapper;

    JsonDataImpl(Object value, JsonpMapper mapper) {
        this.value = value;
        this.mapper = mapper;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public JsonValue toJson() {
        return toJson(null);
    }

    @Override
    public JsonValue toJson(JsonpMapper mapper) {
        if (value instanceof JsonValue) {
            return (JsonValue) value;
        }

        // Provided mapper has precedence over the one that was optionally set at creation time
        mapper = mapper != null ? mapper : this.mapper;
        if (mapper == null) {
            throw new IllegalStateException("Contains a '" + value.getClass().getName() +
                    "' that cannot be converted to a JsonValue without a mapper");
        }

        final JsonParser parser = getParser(mapper);
        parser.next(); // move to first event
        return parser.getValue();
    }

    @Override
    public <T> T to(Class<T> clazz) {
        return to(clazz, null);
    }

    @Override
    public <T> T to(Class<T> clazz, JsonpMapper mapper) {
        if (clazz.isAssignableFrom(value.getClass())) {
            return (T) value;
        }

        mapper = getMapper(mapper);

        JsonParser parser = getParser(mapper);
        return mapper.deserialize(parser, clazz);
    }

    @Override
    public <T> T deserialize(JsonpDeserializer<T> deserializer) {
        return deserialize(deserializer, null);
    }

    @Override
    public <T> T deserialize(JsonpDeserializer<T> deserializer, JsonpMapper mapper) {
        mapper = getMapper(mapper);

        return deserializer.deserialize(getParser(mapper), mapper);
    }

    @Override
    public void serialize(JsonGenerator generator, JsonpMapper mapper) {
        if (value instanceof JsonValue) {
            generator.write((JsonValue) value);
        } else {
            // Mapper provided at creation time has precedence
            (this.mapper != null ? this.mapper : mapper).serialize(value, generator);
        }
    }

    private JsonpMapper getMapper(JsonpMapper localMapper) {
        // Local mapper has precedence over the one provided at creation time
        localMapper = localMapper != null ? localMapper : this.mapper;
        if (localMapper == null) {
            throw new IllegalStateException("A JsonpMapper is needed to convert JsonData");
        }

        return localMapper;
    }

    private JsonParser getParser(JsonpMapper mapper) {
        // FIXME: inefficient roundtrip through a string. Should be replaced by an Event buffer structure.
        StringWriter sw = new StringWriter();
        JsonGenerator generator = mapper.jsonProvider().createGenerator(sw);

        if (value instanceof JsonValue) {
            generator.write((JsonValue) value);
        } else {
            mapper.serialize(value, generator);
        }
        generator.close();

        return mapper.jsonProvider().createParser(new StringReader(sw.toString()));
    }
}