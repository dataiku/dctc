package com.dataiku.dip.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSON {
    public static <T> T parse(String s, Class<T> classOfT) {
        T t = new GsonBuilder().serializeSpecialFloatingPointValues().create().fromJson(s, classOfT);

        // Cleanup-null at the end of array.
        for(Field field: t.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (List.class.isAssignableFrom(field.getType())) {
                try {
                    List<?> l = (List<?>) field.get(t);
                    if (l != null && l.size() > 0) {
                        if(l.get(l.size()-1) == null) {
                            l.remove(l.size()-1);
                        }
                    }
                } catch (IllegalAccessException e) {
                    System.out.println(e);
                }
            }
        }
        return t;
    }
    public static <T> T parseFile(String path, Class<T> classOfT) throws IOException {
        return parse(FileUtils.readFileToString(new File(path), "utf8"), classOfT);
    }
    public static <T> T parseFile(File file, Class<T> classOfT) throws IOException {
        return parse(FileUtils.readFileToString(file, "utf8"), classOfT);
    }

    public static Map<String, String> parseToMap(String s) {
        @SuppressWarnings("unchecked")
        Map<String, String> map = new Gson().fromJson(s, Map.class);
        return map;
    }
    public static String json(Object o) {
        if (o == null) return null;
        return new GsonBuilder().serializeSpecialFloatingPointValues().create().toJson(o);
    }
    public static String pretty(Object o) {
        if (o == null) return null;
        return new GsonBuilder().serializeSpecialFloatingPointValues().setPrettyPrinting().create().toJson(o);
    }
    public static void jsonToFile(Object o, File f) throws IOException {
        FileUtils.write(f, json(o), "utf8");
    }
    public static void prettyToFile(Object o, File f) throws IOException{
        FileUtils.write(f, pretty(o), "utf8");
    }

    public static void prettySyso(Object o) {
        if (o == null) return;
        System.out.println(new GsonBuilder().serializeSpecialFloatingPointValues().setPrettyPrinting().create().toJson(o));
    }
}
