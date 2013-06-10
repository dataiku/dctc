package com.dataiku.dip.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionUtils {
    public static <T> T getMapOnlyValue(Map<? extends Object, T> map) {
        if (map.size() != 1) {
            throw new IllegalArgumentException("Can't get map only value, it has " + map.size() + " elements");
        }
        return map.values().iterator().next();
    }
    
    public static MapAdder appendableSSMap() {
        return new MapAdder(new HashMap<String, String>());
    }
    
    public static class MapAdder {
        private Map<String, String> map;
        public MapAdder(Map<String, String> map) {
            this.map = map;
        }
        public MapAdder put(String name, String value) {
            map.put(name, value);
            return this;
        }
        public Map<String, String> get() {
            return map;
        }
    }
    
    public static <T> void setAddAll(Set<T> set, T[] array) {
        for (T t : array) set.add(t);
    }
    public static <T> void listAddAll(List<T> list, T[] array) {
        for (T t : array) list.add(t);
    }
}
