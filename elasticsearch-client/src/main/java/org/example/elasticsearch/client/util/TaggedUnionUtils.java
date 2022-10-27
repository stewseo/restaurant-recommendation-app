package org.example.elasticsearch.client.util;

import org.example.elasticsearch.client.json.*;


import java.util.*;

public class TaggedUnionUtils {
    public static <Union extends TaggedUnion<Tag, ?>, Tag extends Enum<Tag>, Value> Value get(Union union, Tag kind) {
        if (kind == union._kind()) {
            @SuppressWarnings("unchecked")
            Value result = (Value) union._get();
            return result;
        } else {
            throw new IllegalStateException("Cannot get '" + kind + "' variant: current variant is '" + union._kind() + "'.");
        }
    }

    public static <T> Iterator<?> ndJsonIterator(TaggedUnion<?, T> union) {

        T value = union._get();

        if (value instanceof NdJsonpSerializable) {
            // Iterate on value's items, replacing value, if it appears, by the union. This allows JSON wrapping
            // done by the container to happen.
            Iterator<?> valueIterator = ((NdJsonpSerializable) value)._serializables();

            return new Iterator<Object>() {
                @Override
                public boolean hasNext() {
                    return valueIterator.hasNext();
                }

                @Override
                public Object next() {
                    Object next = valueIterator.next();
                    if (next == value) {
                        return union;
                    } else {
                        return next;
                    }
                }
            };
        } else {
            // Nothing to flatten
            return Collections.singletonList(union).iterator();
        }
    }
}