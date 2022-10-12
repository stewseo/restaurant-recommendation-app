package com.example.client.json;

import com.example.client.util.*;
import jakarta.json.stream.*;

import java.util.function.*;

public class ObjectBuilderDeserializer<T, B extends ObjectBuilder<T>> extends DelegatingDeserializer<T, B> {

    private final JsonpDeserializer<B> builderDeserializer;

    public static <B extends ObjectBuilder<T>, T> JsonpDeserializer<T> lazy(
            Supplier<B> builderCtor,
            Consumer<ObjectDeserializer<B>> builderDeserializerSetup
    ) {
        return new LazyDeserializer<>(() -> {
            ObjectDeserializer<B> builderDeser = new ObjectDeserializer<B>(builderCtor);
            builderDeserializerSetup.accept(builderDeser);
            return new ObjectBuilderDeserializer<>(builderDeser);
        });
    }

    public static <B, T> JsonpDeserializer<T> lazy(
            Supplier<B> builderCtor,
            Consumer<ObjectDeserializer<B>> builderDeserializerSetup,
            Function<B, T> buildFn
    ) {
        return new LazyDeserializer<>(() -> {
            ObjectDeserializer<B> builderDeser = new ObjectDeserializer<B>(builderCtor);
            builderDeserializerSetup.accept(builderDeser);
            return new BuildFunctionDeserializer<>(builderDeser, buildFn);
        });
    }

    public static <T, B extends ObjectBuilder<T>> JsonpDeserializer<T> createForObject(
            Supplier<B> ctor,
            Consumer<ObjectDeserializer<B>> configurer
    ) {
        ObjectDeserializer<B> op = new ObjectDeserializer<>(ctor);
        configurer.accept(op);
        return new ObjectBuilderDeserializer<>(op);
    }

    public ObjectBuilderDeserializer(JsonpDeserializer<B> builderDeserializer) {
        this.builderDeserializer = builderDeserializer;
    }

    @Override
    protected JsonpDeserializer<B> unwrap() {
        return builderDeserializer;
    }

    @Override
    public T deserialize(JsonParser parser, JsonpMapper mapper) {
        ObjectBuilder<T> builder = builderDeserializer.deserialize(parser, mapper);
        return builder == null ? null : builder.build();
    }

    @Override
    public T deserialize(JsonParser parser, JsonpMapper mapper, JsonParser.Event event) {
        ObjectBuilder<T> builder = builderDeserializer.deserialize(parser, mapper, event);
        return builder == null ? null : builder.build();
    }
}