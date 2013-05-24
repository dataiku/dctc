package com.dataiku.dctc;

public class PrettyString {
    public static String quoted(Object msg) {
        return "\"" + msg.toString + "\"";
    }
    public static String squoted(Object msg) {
        return "'" + msg.toString + "'";
    }
    public static String pquoted(Object msg) {
        return "`" + msg.toString + "'";
    }
    public static String nl(String... msgs) {
        String res = "";
        for(String msg: msgs) {
            res += msg + eol();
        }

        return res;
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

}
