package com.dataiku.dctc.display;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Size {
    public static String getReadableSize(long size, String format) {
        NumberFormat formatter = new DecimalFormat(format);
        int indic = 0;
        double s = size;
        while (s > 1024) {
            ++indic;
            s /= 1024;
        }
        String res = unit.substring(indic, indic + 1);
        if (indic == 0) {
            return "" + size;
        }
        return formatter.format(s) + res;
    }
    public static String getReadableSize(long size) {
        return getReadableSize(size, "#0.00");
    }
    private final static String unit = " kMGTP";
}
