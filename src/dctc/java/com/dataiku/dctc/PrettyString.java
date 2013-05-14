package com.dataiku.dctc;

public class PrettyString {
    public static String quoted(String msg) {
        return "\"" + msg + "\"";
    }
    public static String nl(String... msgs) {
        String eol = System.getProperty("line.separator");
        String res = "";
        for(String msg: msgs) {
            res += msg + eol;
        }

        return res;
    }
}
