package com.dataiku.dctc.command;

class StringGrepMatcher implements GrepMatcher {
    public StringGrepMatcher(String pattern) {
        this.pattern = pattern;
    }

    public boolean match(String line) {
        return idx(line) != -1;
    }
    public int idx(String line) {
        return line.indexOf(pattern);
    }
    public int len(String line) {
        return pattern.length();
    }

    // Getters/Setters
    public String getPattern() {
        return pattern;
    }
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
    public StringGrepMatcher withPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    // Attributes
    private String pattern;
}
