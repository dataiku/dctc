package com.dataiku.dctc.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RatExpGrepMatcher implements GrepMatcher {
    RatExpGrepMatcher(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    public int begin(String line) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return matcher.start();
        }
        else {
            return -1;
        }
    }
    public int end(String line) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return matcher.end();
        }
        else {
            return -1;
        }
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
