package com.dataiku.dip.utils;

public class BooleanUtils {
    static public Boolean toBoolean(String b) {
        String lowerValue = b.toLowerCase();
        if ("yes".startsWith(lowerValue)
            || "true".startsWith(lowerValue)
            || "1".equals(lowerValue)) {
            return true;
        }
        else if ("no".startsWith(lowerValue)
                 || "false".startsWith(lowerValue)
                 || "0".equals(lowerValue)) {
            return false;
        } else {
            return null;
        }
    }
    static public Boolean toBoolean(String b, Boolean defaultValue) {
        Boolean res = toBoolean(b);
        if (res == null) {
            return defaultValue;
        }
        else {
            return res;
        }
    }
    static public boolean toboolean(String b) {
        Boolean res = toBoolean(b);
        return (res != null) && res;
    }
}
