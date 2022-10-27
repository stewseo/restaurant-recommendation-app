package org.example.elasticsearch.client.json;


import jakarta.json.stream.*;
import java.util.*;

public abstract class DelegatingDeserializer<T, U> implements JsonpDeserializer<T> {

    protected abstract JsonpDeserializer<U> unwrap();

    @Override
    public EnumSet<JsonParser.Event> nativeEvents() {
        return unwrap().nativeEvents();
    }

    @Override
    public EnumSet<JsonParser.Event> acceptedEvents() {
        return unwrap().acceptedEvents();
    }

    public abstract static class SameType<T> extends DelegatingDeserializer<T, T> {
        @Override
        public T deserialize(JsonParser parser, JsonpMapper mapper) {
            return unwrap().deserialize(parser, mapper);
        }

        @Override
        public T deserialize(JsonParser parser, JsonpMapper mapper, JsonParser.Event event) {
            return unwrap().deserialize(parser, mapper, event);
        }
    }

    public static JsonpDeserializer<?> unwrap(JsonpDeserializer<?> deserializer) {
        while (deserializer instanceof DelegatingDeserializer) {
            deserializer = ((DelegatingDeserializer<?, ?>) deserializer).unwrap();
        }
        return deserializer;
    }
}