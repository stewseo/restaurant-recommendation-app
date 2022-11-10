package org.example.elasticsearch.client.util;

import jakarta.json.stream.*;
import org.example.elasticsearch.client.json.*;

import java.util.*;
import java.util.function.*;

public class Pair<K, V> {

    private final K name;
    private final V value;

    public Pair(K name, V value) {
        this.name = name;
        this.value = value;
    }

    public K key() {
        return this.name;
    }

    public V value() {
        return this.value;
    }

    public static <K, V> Pair<K, V> of(K name, V value) {
        return new Pair<>(name, value);
    }

    public static <K, V> JsonpDeserializer<Pair<K, V>> deserializer(
            Function<String, K> keyDeserializer, JsonpDeserializer<V> valueDeserializer
    ) {
        return new JsonpDeserializerBase<Pair<K, V>>(EnumSet.of(JsonParser.Event.START_OBJECT)) {
            @Override
            public Pair<K, V> deserialize(JsonParser parser, JsonpMapper mapper, JsonParser.Event event) {

                JsonpUtils.expectNextEvent(parser, JsonParser.Event.KEY_NAME);
                String name = parser.getString();

                try {
                    K key = keyDeserializer.apply(parser.getString());
                    V value = valueDeserializer.deserialize(parser, mapper);
                    JsonpUtils.expectNextEvent(parser, JsonParser.Event.END_OBJECT);

                    return new Pair<>(key, value);
                } catch (Exception e) {
                    throw JsonpMappingException.from(e, null, name, parser);
                }
            }
        };
    }
}