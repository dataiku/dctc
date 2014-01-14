package com.dataiku.dip.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.Map;

public class JSON {

    private final static GsonBuilder gsonBuilder = createGsonBuilder();
    private final static GsonBuilder gsonBuilderPretty = createGsonBuilder().setPrettyPrinting();

    private static GsonBuilder createGsonBuilder() {
        return new GsonBuilder()
                .serializeSpecialFloatingPointValues() // Allows for NaN and stuff
                .registerTypeAdapterFactory(new GsonLowerCaseEnumTypeAdapterFactor()) // lower-case normalisation when serializing/deserializing enum
        ;
    }

    public static void registerAdapter(Type type, Object adapter) {
        gsonBuilder.registerTypeAdapter(type, adapter);

        gsonBuilderPretty.registerTypeAdapter(type, adapter);
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
                // we enter the string state
                String stringToken = tokener.nextString(c);
                writer.write(JSONObject.quote(stringToken));
            }
            else if (c==',') {
                // getting rid of trailing commma in array
                char afterComma = tokener.nextClean();
                if ((afterComma == '}') || (afterComma == ']'))   {
                    writer.write(afterComma);
                }
                else {
                    writer.write(c);
                    tokener.back();
                }
            }
            else if (c == '/') {
                // we enter the comment state.
                c = tokener.next();
                if (c == '/') {
                    // one line comment
                    // we skip until the next new line.
                    tokener.skipTo('\n');
                }
                else if (c == '*') {
                    while (tokener.more()) {
                        c = tokener.skipTo('*');
                        if (c != '*') {
                            throw tokener.syntaxError("Unclosed comment");
                        }
                        tokener.next();
                        c = tokener.next();
                        if (c == '/') {
                            break;
                        }
                        tokener.back(); //< handle the case of the **/
                    }
                }
                else {
                    throw tokener.syntaxError("Invalid character: " + c + "after '/'");
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
        try {
            String sanitized = sanitizeJson(s);
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
    
    public static <T> T deepCopy(T o) {
        if(o==null) {
            return null;
        }
        return (T) JSON.parse(JSON.json(o), o.getClass());
    }
}