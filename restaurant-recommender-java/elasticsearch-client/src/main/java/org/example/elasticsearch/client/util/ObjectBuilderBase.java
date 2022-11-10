package org.example.elasticsearch.client.util;

import org.example.lowlevel.restclient.*;

import java.util.*;

public class ObjectBuilderBase {
    private boolean _used = false;

    protected void _checkSingleUse() {
        PrintUtils.cyan("_checkSingleUse ");
        if (this._used) {
            throw new IllegalStateException("Object builders can only be used once");
        }
        this._used = true;
    }

    //----- List utilities

    static final class InternalList<T> extends ArrayList<T> {
        InternalList() {
        }

        InternalList(Collection<? extends T> c) {
            super(c);
        }
    };

    private static <T> List<T> _mutableList(List<T> list) {
        PrintUtils.cyan("_mutableList ");

        if (list == null) {
            return new InternalList<>();
        } else if (list instanceof InternalList) {
            return list;
        } else {
            // Adding to a list we don't own: make a defensive copy, also ensuring it is mutable.
            return new InternalList<>(list);
        }
    }

    @SafeVarargs
    protected static <T> List<T> _listAdd(List<T> list, T value, T... values) {
        PrintUtils.cyan("_listAdd ");

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

    //----- Map utilities

    private static final class InternalMap<K, V> extends HashMap<K, V> {
        InternalMap() {
        }

        InternalMap(Map<? extends K, ? extends V> m) {
            super(m);
        }
    }

    private static <K, V> Map<K, V> _mutableMap(Map<K, V> map) {
        PrintUtils.cyan("_mutableMap ");

        if (map == null) {
            return new InternalMap<>();
        } else if (map instanceof InternalMap) {
            return map;
        } else {
            // Adding to a map we don't own: make a defensive copy, also ensuring it is mutable.
            return new InternalMap<>(map);
        }
    }

    protected static <K, V> Map<K, V> _mapPut(Map<K, V> map, K key, V value) {
        PrintUtils.cyan("_mapPut ");

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