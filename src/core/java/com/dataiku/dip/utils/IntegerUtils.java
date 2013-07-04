package com.dataiku.dip.utils;

public class IntegerUtils {
    static public int toInt(String v) {
        if (v.isEmpty()) {
            return 0;
        }
        if (v.startsWith("+")) {
            return toInt(v.substring(1));
        }
        if (v.startsWith("-")) {
            return - toInt(v.substring(1));
        }

        int base = 10;
        if (v.startsWith("0x") || v.startsWith("0X")) {
            base = 16;
            v = v.substring(2);
        }
        else if (v.startsWith("0")) {
            base = 8;
            v = v.substring(1);
        }
        else {
            base = 10;
        }
        v = v.replaceAll("_", ""); // Allow to write 1_000, 1_00_00...

        return Integer.parseInt(v, base);
    }
}
