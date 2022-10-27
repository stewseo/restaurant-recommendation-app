package org.example.elasticsearch.client.json;

import jakarta.json.stream.*;

import java.util.*;

public class NamedDeserializer<T> implements JsonpDeserializer<T> {

    private static final EnumSet<JsonParser.Event> events = EnumSet.of(
            JsonParser.Event.START_OBJECT,
            JsonParser.Event.START_ARRAY,
            JsonParser.Event.VALUE_FALSE,
            JsonParser.Event.VALUE_TRUE,
            JsonParser.Event.VALUE_NUMBER,
            JsonParser.Event.VALUE_STRING,
            JsonParser.Event.VALUE_NULL
    );

    private final String name;

    public NamedDeserializer(String name) {
        this.name = name;
    }

    @Override
    public EnumSet<JsonParser.Event> nativeEvents() {
        return events;
    }

    @Override
    public EnumSet<JsonParser.Event> acceptedEvents() {
        return events;
    }

    @Override
    public T deserialize(JsonParser parser, JsonpMapper mapper) {
        JsonpDeserializer<T> deserializer = mapper.attribute(name);
        if (deserializer == null) {
            throw new JsonpMappingException("Missing deserializer for generic type: " + name, parser.getLocation());
        }
        return deserializer.deserialize(parser, mapper);
    }

    @Override
    public T deserialize(JsonParser parser, JsonpMapper mapper, JsonParser.Event event) {
        JsonpDeserializer<T> deserializer = mapper.attribute(name);
        if (deserializer == null) {
            throw new JsonpMappingException("Missing deserializer for generic type: " + name, parser.getLocation());
        }
        return deserializer.deserialize(parser, mapper, event);
    }
}