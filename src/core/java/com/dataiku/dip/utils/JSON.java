package com.dataiku.dip.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONStringer;
import org.json.JSONTokener;

public class JSON {

    private final static GsonBuilder gsonBuilder = new GsonBuilder().serializeSpecialFloatingPointValues();
    private final static GsonBuilder gsonBuilderPretty = new GsonBuilder().serializeSpecialFloatingPointValues().setPrettyPrinting();

    public static void registerAdapter(Type type, Object adapter) {
        gsonBuilder.registerTypeAdapter(type, adapter);

        gsonBuilderPretty.registerTypeAdapter(type, adapter);
    }

    public static void registerHierarchyAdapter(Class<?> type, Object adapter) {
        gsonBuilder.registerTypeHierarchyAdapter(type, adapter);
        gsonBuilderPretty.registerTypeHierarchyAdapter(type, adapter);
    }

    public static Gson gson() {
        return gsonBuilder.create();
    }

    public static Gson gsonPretty() {
        return gsonBuilderPretty.create();
    }


    /**
     * Removing trailing commas in arrray instead
     * of interpreting them as an extra null element.
     */
    public static String sanitizeJson(String json) throws JSONException {
        JSONTokener tokener = new JSONTokener(json);
        StringWriter writer = new StringWriter();
        while (tokener.more() ) {
           char c = tokener.next();
           if ((c=='"') || (c=='\'')) {
               String stringToken = tokener.nextString(c);
               JSONArray arr = new JSONArray(new String[]{stringToken});
               String arrJson = arr.toString();
               writer.write("\n");
               writer.write(arrJson.toString().substring(1, arrJson.length()-1));
               writer.write("\n");
           }
           else if (c==',') {
               char afterComma = tokener.nextClean();
               if ((afterComma == '}') || (afterComma == ']'))   {
                   writer.write(afterComma);
               }
               else {
                   writer.write(c);
                   tokener.back();
               }
           }
           else {
               writer.write(c);
           }
        }
        return writer.toString(); // gson does not like long lines
    }


    /**
     * Human friendly parser of json.
     * Handles trailing comma in array gracefully.
     *
     * @param s json string to be parsed
     * @param classOfT type to be parsed
     * @return The object of typed T that has been parsed.
     */
    public static <T> T parse(String s, Class<T> classOfT) {
        String sanitized = null;
        try {
            sanitized = sanitizeJson(s);
            return gson().fromJson(sanitized, classOfT);
        } catch (JSONException e) {
            throw new JsonSyntaxException("Sanitization failed", e);
        }
    }

    public static <T> T parseFile(String path, Class<T> classOfT) throws IOException {
        return parse(FileUtils.readFileToString(new File(path), "utf8"), classOfT);
    }
    public static <T> T parseFile(File file, Class<T> classOfT) throws IOException {
        return parse(FileUtils.readFileToString(file, "utf8"), classOfT);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> parseToMap(String s) {
        return gson().fromJson(s, Map.class);
    }

    public static String json(Object o) {
        if (o == null) return null;
        return gson().toJson(o);
    }

    public static String pretty(Object o) {
        if (o == null) return null;
        return gsonPretty().toJson(o);
    }
    public static void jsonToFile(Object o, File f) throws IOException {
        FileUtils.write(f, json(o), "utf8");
    }
    public static void prettyToFile(Object o, File f) throws IOException{
        FileUtils.write(f, pretty(o), "utf8");
    }

    public static void prettySyso(Object o) {
        if (o == null) return;
        System.out.println(pretty(o));
    }
}
