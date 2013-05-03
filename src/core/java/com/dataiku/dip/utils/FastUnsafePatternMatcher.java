package com.dataiku.dip.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FastUnsafePatternMatcher {
    Matcher m = null;
    public FastUnsafePatternMatcher(Pattern compiled) {
        m = compiled.matcher("");
    }
    public FastUnsafePatternMatcher(String pattern) {
        m = Pattern.compile(pattern).matcher("");
    }
    public boolean matches(String v) {
        m.reset(v);
        return m.matches();
    }
    public boolean find(String v) {
        m.reset(v);
        return m.find();
    }
    public Matcher matcher(String v) {
        m.reset(v);
        return m;
    }
}
