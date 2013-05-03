package com.dataiku.dip.utils;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LazyHashMap<K, V> implements Map<K, V>{
    Map<K, V> backend = new HashMap<K, V>();

    Class<V> clazz;
    Constructor<V> ctor;

    public LazyHashMap(Class<V> clazz) {
        this.clazz = clazz;
        try {
            this.ctor = clazz.getConstructor();
        } catch (Exception e) {
            throw new Error("Can't create lazy hash map", e);
        }
    }
    
    public V getOrCreate(K key) {
        V ret = backend.get(key);
        if (ret == null) {
            try {
                ret = ctor.newInstance();
            } catch (Exception e) {
                throw new Error("Can't create lazy hash map item", e);
            }
            backend.put(key, ret);
        }
        return ret;
    }

    @Override
    public void clear() {
        backend.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return backend.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return backend.containsValue(value);
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return backend.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return backend.equals(o);
    }

    @Override
    public V get(Object key) {
        return backend.get(key);
    }

    @Override
    public int hashCode() {
        return backend.hashCode();
    }

    @Override
    public boolean isEmpty() {
        return backend.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return backend.keySet();
    }

    @Override
    public V put(K key, V value) {
        return backend.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        backend.putAll(m);
    }

    @Override
    public V remove(Object key) {
        return backend.remove(key);
    }

    @Override
    public int size() {
        return backend.size();
    }

    @Override
    public Collection<V> values() {
        return backend.values();
    }
}
