package com.dataiku.dctc.display;

public class Date {
    public static String getReadableDate(int time) {
        String res = "";
        for (int i = 0; i < 3; ++i) {
            if (time == 0) {
                if (i < 2) {
                    res = "0" + unit[i] + res;
                }
                break;
            } else {
                int t = time % div[i];
                time /= div[i];
                res = "" + t + unit[i] + res;
            }
        }
        return res;
    }

    private static int[] div = { 60, 60, 24, 7, 4, 12 };
    private static String[] unit = { "s", "m", "h", "w", "m", "y" };
}
