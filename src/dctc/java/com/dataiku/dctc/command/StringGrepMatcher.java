package com.dataiku.dctc.command;

class StringGrepMatcher implements GrepMatcher {
    public StringGrepMatcher(String pattern) {
        this.pattern = pattern;
    }

    public boolean match(String line) {
        return begin(line) != -1;
    }
    public int begin(String line) {
        return line.indexOf(pattern);
    }
    public int end(String line) {
        return pattern.length() + line.indexOf(pattern);
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
