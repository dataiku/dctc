package com.dataiku.dip.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FastSafePatternMatcher {
    ThreadLocal<Matcher> m = new ThreadLocal<Matcher>();
    Pattern compiled;
    public FastSafePatternMatcher(Pattern compiled) {
        this.compiled = compiled;
    }
    public boolean matches(String v) {
        Matcher matcher = m.get();
        if (matcher == null) {
            matcher = compiled.matcher(v);
            m.set(matcher);
            return matcher.matches();
        } else {
            matcher.reset(v);
            return matcher.matches();
        }
    }
}
