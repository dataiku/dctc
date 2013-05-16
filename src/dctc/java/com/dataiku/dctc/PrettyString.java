package com.dataiku.dctc;

public class PrettyString {
    public static String quoted(String msg) {
        return "\"" + msg + "\"";
    }
    public static String squoted(String msg) {
        return "'" + msg + "'";
    }
    public static String pquoted(String msg) {
        return "`" + msg + "'";
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
    public static String cat(String concatenateStr, String... msg) {
        StringBuilder b = new StringBuilder();
        if (msg.length > 0) {
            b.append(msg[0]);
            for(int i = 1; i < msg.length; ++i) {
                b.append(concatenateStr);
                b.append(msg[i]);
            }
        }
        return b.toString();
    }
    public static String scat(String... msg) {
        return cat(" ", msg);
    }
    // Do not put a trailing eol.
    public static String nlcat(String... msg) {
        return cat(eol(), msg);
    }

}
