package org.example.elasticsearch.client.util;

import org.slf4j.*;

import java.util.*;

public class ObjectBuilderBase {

    public static final String GREEN = "\u001B[32m";
    public static final String RESET = "\033[0m";
    private static final Logger logger = LoggerFactory.getLogger(ObjectBuilderBase.class);
    private boolean _used = false;

    protected void _checkSingleUse() {
        if (this._used) {
            throw new IllegalStateException("Object builders can only be used once");
        }
        this._used = true;
    }
    static final class InternalList<T> extends ArrayList<T> {
        InternalList() {
            logger.info("{}static final class InternalList<T> extends ArrayList<T>(): {}%n",
                    GREEN,
                    RESET);
        }

        InternalList(Collection<? extends T> c) {
            super(c);
        }

    };

    private static <T> List<T> _mutableList(List<T> list) {
        if (list == null) {
            return new InternalList<>();
        } else if (list instanceof InternalList) {
            return list;
        } else {
            // Adding to a list we don't own: make a defensive copy, also ensuring that
            // objects can be changed to any value or state without adding a new object
            return new InternalList<>(list);
        }
    }

    // a programmer assertion that the body of the annotated method or constructor does not perform potentially unsafe operations on its varargs parameter.
    // Applying this annotation to a method or constructor suppresses unchecked warnings about a non-reifiable variable arity (vararg) type
    // and suppresses unchecked warnings about parameterized array creation at call sites
    @SafeVarargs
    protected static <T> List<T> _listAdd(List<T> list, T value, T... values) {
        list = _mutableList(list);
        list.add(value);
        if (values.length > 0) {
            list.addAll(Arrays.asList(values));
        }
        return list;
    }

    protected static <T> List<T> _listAddAll(List<T> list, List<T> values) {
        if (list == null) {
            // Keep the original list to avoid an unnecessary copy.
            // It will be copied if we add more values.
            return Objects.requireNonNull(values);
        } else {
            list = _mutableList(list);
            list.addAll(values);
            return list;
        }
    }

    private static final class InternalMap<K, V> extends HashMap<K, V> {
        InternalMap() {
        }

        InternalMap(Map<? extends K, ? extends V> m) {
            super(m);
        }
    }
    
    private static <K, V> Map<K, V> _mutableMap(Map<K, V> map) {
        if (map == null) {
            return new ObjectBuilderBase.InternalMap<>();
        } else if (map instanceof ObjectBuilderBase.InternalMap) {
            return map;
        } else {
            // Adding to a map we don't own: make a defensive copy, also ensuring it is mutable.
            return new ObjectBuilderBase.InternalMap<>(map);
        }
    }
    
    protected static <K, V> Map<K, V> _mapPut(Map<K, V> map, K key, V value) {
        map = _mutableMap(map);
        map.put(key, value);
        return map;
    }
    
    protected static <K, V> Map<K, V> _mapPutAll(Map<K, V> map, Map<K, V> entries) {
        if (map == null) {
            // Keep the original map to avoid an unnecessary copy.
            // It will be copied if we add more entries.
            return Objects.requireNonNull(entries);
        } else {
            map = _mutableMap(map);
            map.putAll(entries);
            return map;
        }
    }
}