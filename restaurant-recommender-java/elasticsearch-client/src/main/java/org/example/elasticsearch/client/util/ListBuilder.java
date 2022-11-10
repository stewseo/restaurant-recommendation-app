package org.example.elasticsearch.client.util;

import java.util.*;
import java.util.function.*;

public class ListBuilder<T, B> implements ObjectBuilder<List<T>> {
    private final List<T> list = new ArrayList<>();
    private final Supplier<B> builderCtor;

    public static <T, B extends ObjectBuilder<T>> ListBuilder<T, B> of(Supplier<B> builderCtor) {
        return new ListBuilder<>(builderCtor);
    }

    public ListBuilder(Supplier<B> builderCtor) {
        this.builderCtor = builderCtor;
    }

    public ListBuilder<T, B> add(T value) {
        list.add(value);
        return this;
    }

    public ListBuilder<T, B> add(Function<B, ObjectBuilder<T>> fn) {
        return add(fn.apply(builderCtor.get()).build());
    }

    public ListBuilder<T, B> addAll(Iterable<? extends T> iterable) {
        for (T item: iterable) {
            list.add(item);
        }
        return this;
    }

    @Override
    public List<T> build() {
        return list;
    }
}