package org.example.elasticsearch.client.json;

import java.util.function.*;

public class LazyDeserializer<T> extends DelegatingDeserializer.SameType<T> {

    private final Supplier<JsonpDeserializer<T>> ctor;
    private volatile JsonpDeserializer<T> deserializer = null;

    public LazyDeserializer(Supplier<JsonpDeserializer<T>> ctor) {
        this.ctor = ctor;
    }

    protected JsonpDeserializer<T> unwrap() {
        // See SEI CERT LCK10-J https://wiki.sei.cmu.edu/confluence/x/6zdGBQ
        JsonpDeserializer<T> d = deserializer;
        if (d != null) {
            return d;
        } else {
            synchronized (this) {
                if (deserializer == null) {
                    deserializer = ctor.get();
                }
            }
            return deserializer;
        }
    }
}
