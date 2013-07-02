package com.dataiku.dctc.command;

class IgnoreCaseGrepMatcher implements GrepMatcher {
    IgnoreCaseGrepMatcher(GrepMatcher matcher, String pattern) {
        this.matcher = matcher;
        this.matcher.setPattern(pattern.toLowerCase());
    }

    public boolean match(String line) {
        return matcher.match(line.toLowerCase());
    }
    public int idx(String line) {
        return matcher.idx(line.toLowerCase());
    }
    public int len(String line) {
        return matcher.len(line.toLowerCase());
    }

    // Getters/Setters
    public GrepMatcher getMatcher() {
        return matcher;
    }
    public void setMatcher(GrepMatcher matcher) {
        this.matcher = matcher;
    }
    public IgnoreCaseGrepMatcher withMatcher(GrepMatcher matcher) {
        this.matcher = matcher;
        return this;
    }
    public String getPattern() {
        return matcher.getPattern();
    }
    public void setPattern(String pattern) {
        matcher.setPattern(pattern.toLowerCase());
    }
    public IgnoreCaseGrepMatcher withPattern(String pattern) {
        setPattern(pattern);
        return this;
    }

    // Attributes
    private GrepMatcher matcher;
}
