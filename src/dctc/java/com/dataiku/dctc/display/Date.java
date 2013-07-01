package com.dataiku.dctc.display;

public class Date {
    public static String getReadableDate(int time) {
        return getReadableDate(time, true);
    }
    public static String getReadableDate(int time, boolean fill) {
        String res = "";
        for (int i = 0; i < div.length; ++i) {
            if (time == 0) {
                if (i < 2 && fill) {
                    res = "0" + unit.charAt(i) + res;
                }
                break;
            }
            else {
                int t = time % div[i];
                time /= div[i];
                res = (t < 10 ? "0" : "") + t + unit.charAt(i) + res;
            }
        }
        return res;
    }

    private static int[] div = { 60, 60, 24, 7, 4, 12 };
    private static String unit = "smhwmy";
}
