package com.dataiku.dctc.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RatExpGrepMatcher implements GrepMatcher {
    RatExpGrepMatcher(String[] pattern) {
        setPattern(pattern);
    }

    public int begin(String line) {
        for (Pattern pat: pattern) {
            Matcher matcher = pat.matcher(line);
            if (matcher.find()) {
                return matcher.start();
            }
        }
        return -1;
    }
    public int end(String line) {
        for (Pattern pat: pattern) {
            Matcher matcher = pat.matcher(line);
            if (matcher.find()) {
                return matcher.end();
            }
        }
        return -1;
    }
    public boolean match(String line) {
        for (Pattern pat: pattern) {
            if (pat.matcher(line).find()) {
                return true;
            }
        }
        return false;
    }
    // Getters-Setters
    public String[] getPattern() {
        String[] pattern = new String[this.pattern.length];
        for (int i = 0; i < pattern.length; ++i) {
            pattern[i] = this.pattern[i].pattern();
        }
        return pattern;
    }
    public void setPattern(String[] pattern) {
        this.pattern = new Pattern[pattern.length];
        for (int i = 0; i < pattern.length; ++i) {
            this.pattern[i] = Pattern.compile(pattern[i]);
        }
    }
    public RatExpGrepMatcher withPattern(String[] pattern) {
        setPattern(pattern);
        return this;
    }

    // Attributes
    private Pattern[] pattern;
}
