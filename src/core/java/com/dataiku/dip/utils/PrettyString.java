package com.dataiku.dip.utils;

public class PrettyString {
    // Helper for display
    public static boolean isInteractif() {
        return System.console() != null;
    }
    public static boolean displayWithColor() {
        return isInteractif() || System.getenv("CLICOLOR_FORCE") != null;
    }

    public static String quoted(Object msg) {
        return "\"" + msg.toString() + "\"";
    }
    public static String squoted(Object msg) {
        return "'" + msg.toString() + "'";
    }
    public static String pquoted(Object msg) {
        return "`" + msg.toString() + "'";
    }
    public static String nl(String... msgs) {
        StringBuilder sb = new StringBuilder();
        for(String msg: msgs) {
            sb.append(msg);
            sb.append(eol());
        }

        return sb.toString();
    }
    public static String eol() {
        return System.getProperty("line.separator");
    }
    protected static String cat(String concatenateStr, Object... msg) {
        StringBuilder b = new StringBuilder();
        if (msg.length > 0) {
            b.append(msg[0].toString());
            for(int i = 1; i < msg.length; ++i) {
                b.append(concatenateStr);
                b.append(msg[i]);
            }
        }
        return b.toString();
    }
    public static String scat(Object... msg) {
        return cat(" ", msg);
    }
    // Do not put a trailing eol.
    public static String nlcat(Object... msg) {
        return cat(eol(), msg);
    }

    // Color method
    public static String format(String str, String color) {
        if (displayWithColor()) {
            return "\u001B[" + color + "m" + str + "\u001B[0m";
        }
        else {
            return str;
        }
    }
    public static String red(String str) {
        return format(str, "31");
    }
}
