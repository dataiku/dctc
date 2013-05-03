package com.dataiku.dip.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.NDC;

/**
 * A very simple thread-local stack of error chunks, that can be used to prepend in exception messages
 * (quite like NDC in log4j)
 *
 * ErrorContext.push("processor A");
 * ErrorContext.push("transformation B");
 * ErrorContext.throwIAE("Missing param 'pouet'");
 * --> java.lang.IllegalArgumentException: in processor A: in transformation B: missing param 'pouet'
 */
public class ErrorContext {
    static ThreadLocal<List<String>> chunks = new ThreadLocal<List<String>>();

    public static void pushWithNDC(String chunk) {
        push(chunk);
        NDC.push(chunk);
    }
    public static void popWithNDC() {
        pop();
        NDC.pop();
    }

    public static void push(String chunk) {
        if (chunks.get() == null) chunks.set(new ArrayList<String>());
        chunks.get().add(chunk);
    }

    public static void pop() {
        if (chunks.get() == null) chunks.set(new ArrayList<String>());
        if (chunks.get().size() > 0) {
            chunks.get().remove(chunks.get().size() - 1);
        }
    }

    public static void clear() {
        if (chunks.get() != null) chunks.get().clear();
    }

    public static String format() {
        if (chunks.get() == null) chunks.set(new ArrayList<String>());
        List<String> lchunks = chunks.get();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lchunks.size(); i++) {
            sb.append("in " + lchunks.get(i));
            sb.append(": ");
        }
        return sb.toString();
    }

    public static String checkNotEmpty(String obj, String details) {
        if (obj == null) {
            throw iae("Unexpected null value for " + details);
        }
        if (obj.length() == 0) {
            throw iae("Unexpected empty value for "+ details);
        }
        return obj;
    }
    public static <T> T checkNotNull(T obj) {
        if (obj == null) {
            throw iae("Unexpected null");
        }
        return obj;
    }

    public static <T> T checkNotNull(T obj, String message) {
        if (obj == null) {
            throw iae(message);
        }
        return obj;
    }

    public static IllegalArgumentException iae(String message) {
        return  new IllegalArgumentException(format() + message);
    }
    public static IllegalArgumentException iaef(String message, Object ... format) {
        return  new IllegalArgumentException(format() + String.format(message, format));
    }

    public static <T> T throwIAE(String message) {
        throw new IllegalArgumentException(format() + message);
    }

}
