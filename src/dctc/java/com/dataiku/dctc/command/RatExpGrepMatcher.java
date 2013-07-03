package com.dataiku.dctc.command;

import java.util.regex.Pattern;

class RatExpGrepMatcher implements GrepMatcher {
    RatExpGrepMatcher(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    public int idx(String line) {
        return pattern.matcher(line).regionStart();

    }
    public int len(String line) {
        return pattern.matcher(line).regionEnd();
    }
    public boolean match(String line) {
        return pattern.matcher(line).find();
    }
    // Getters-Setters
    public String getPattern() {
        return pattern.pattern();
    }
    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }
    public RatExpGrepMatcher withPattern(String pattern) {
        setPattern(pattern);
        return this;
    }

    // Attributes
    private Pattern pattern;
}

