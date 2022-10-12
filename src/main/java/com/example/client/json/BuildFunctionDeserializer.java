package com.example.client.json;

import jakarta.json.stream.*;

import java.util.function.*;

public class BuildFunctionDeserializer<B, T> extends DelegatingDeserializer<T, B> {

    private final JsonpDeserializer<B> builderDeserializer;
    private final Function<B, T> build;

    public BuildFunctionDeserializer(JsonpDeserializer<B> builderDeserializer, Function<B, T> build) {
        this.builderDeserializer = builderDeserializer;
        this.build = build;
    }

    @Override
    protected JsonpDeserializer<B> unwrap() {
        return builderDeserializer;
    }

    @Override
    public T deserialize(JsonParser parser, JsonpMapper mapper) {
        B builder = builderDeserializer.deserialize(parser, mapper);
        return builder == null ? null : build.apply(builder);
    }

    @Override
    public T deserialize(JsonParser parser, JsonpMapper mapper, JsonParser.Event event) {
        B builder = builderDeserializer.deserialize(parser, mapper, event);
        return builder == null ? null : build.apply(builder);
    }
}