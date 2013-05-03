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
        String unit = " kMGTP"; // octect, kilo, giga…
        String res = unit.substring(indic, indic + 1);
        if (indic == 0) {
            res = "";
        }
        return formatter.format(s) + res;
    }
    public static String getReadableSize(long size) {
        return getReadableSize(size, "#0.00");
    }
}