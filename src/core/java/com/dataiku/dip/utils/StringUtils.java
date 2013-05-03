package com.dataiku.dip.utils;

import java.util.List;

public class StringUtils {
    /**
     * Allocation-optimized string splitting
     * Strongly taken from Apache Commons-Lang
     */
    public static void split(String str, char separatorChar, boolean preserveAllTokens, List<String> list) {
        list.clear();
        if (str == null) return;
        int len = str.length();
        if (len == 0) return;
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match || preserveAllTokens) {
                    list.add(str.substring(start, i));
                    match = false;
                    lastMatch = true;
                }
                start = ++i;
                continue;
            } else {
                lastMatch = false;
            }
            match = true;
            i++;
        }
        if (match || (preserveAllTokens && lastMatch)) {
            list.add(str.substring(start, i));
        }
    }
}
