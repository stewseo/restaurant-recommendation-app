package com.example.client.util;


import co.elastic.clients.util.*;

import javax.annotation.*;
import java.util.*;

public class ApiTypeHelper {

    private ApiTypeHelper() {}

    private static final ThreadLocal<Boolean> disableRequiredPropertiesCheck = new ThreadLocal<>();

    public static boolean requiredPropertiesCheckDisabled() {
        return disableRequiredPropertiesCheck.get() == Boolean.TRUE;
    }

    public static class DisabledChecksHandle implements AutoCloseable {
        private final Boolean value;

        public DisabledChecksHandle(Boolean value) {
            this.value = value;
        }
        @Override
        public void close() {
            disableRequiredPropertiesCheck.set(value);
        }
    }

    public static DisabledChecksHandle DANGEROUS_disableRequiredPropertiesCheck(boolean disable) {
        DisabledChecksHandle result = new DisabledChecksHandle(disableRequiredPropertiesCheck.get());
        disableRequiredPropertiesCheck.set(disable);
        return result;
    }

    /** Immutable empty list implementation so that we can create marker list objects */
    static class EmptyList extends AbstractList<Object> {
        @Override
        public Object get(int index) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public Iterator<Object> iterator() {
            return Collections.emptyIterator();
        }
    };

    static final List<Object> UNDEFINED_LIST = new EmptyList();

    @SuppressWarnings("unchecked")
    public static <T> List<T> undefinedList() {
        return (List<T>)UNDEFINED_LIST;
    }


    public static <T> boolean isDefined(List<T> list) {
        return list != null && list != UNDEFINED_LIST;
    }


    public static <T> List<T> unmodifiable(@Nullable List<T> list) {
        if (list == null) {
            return undefinedList();
        }
        if (list == UNDEFINED_LIST) {
            return list;
        }
        return Collections.unmodifiableList(list);
    }


    public static <T> List<T> unmodifiableRequired(List<T> list, Object obj, String name) {
        // We only check that the list is defined and not that it has some elements, as empty required
        // lists may have a meaning in some APIs. Furthermore, being defined means that it was set by
        // the application, so it's not an omission.

        return Collections.unmodifiableList(list);
    }

    static class EmptyMap extends AbstractMap<Object, Object> {
        @Override
        public Set<Entry<Object, Object>> entrySet() {
            return Collections.emptySet();
        }
    }

    static final Map<Object, Object> UNDEFINED_MAP = new EmptyMap();

    public static <T> T requireNonNull(T value, Object obj, String name) {
        if (value == null && !requiredPropertiesCheckDisabled()) {
            throw new MissingRequiredPropertyException(obj, name);
        }
        return value;
    }


    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> undefinedMap() {
        return (Map<K, V>)UNDEFINED_MAP;
    }


    public static <K, V> boolean isDefined(Map<K, V> map) {
        return map != null && map != UNDEFINED_MAP;
    }


    public static <K, V> Map<K, V> unmodifiable(Map<K, V> map) {
        if (map == null) {
            return undefinedMap();
        }
        if (map == UNDEFINED_MAP) {
            return map;
        }
        return Collections.unmodifiableMap(map);
    }

    public static <K, V> Map<K, V> unmodifiableRequired(Map<K, V> map, Object obj, String name) {
        // We only check that the map is defined and not that it has some elements, as empty required
        // maps may have a meaning in some APIs. Furthermore, being defined means that it was set by
        // the application, so it's not an omission.
        return Collections.unmodifiableMap(map);
    }
}
