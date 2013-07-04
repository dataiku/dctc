package com.dataiku.dctc.command.grep;

class IgnoreCaseGrepMatcher implements GrepMatcher {
    IgnoreCaseGrepMatcher(GrepMatcher matcher) {
        this.matcher = matcher;
        setPattern(matcher.getPattern());
    }

    public boolean match(String line) {
        return matcher.match(line.toLowerCase());
    }
    public int begin(String line) {
        return matcher.begin(line.toLowerCase());
    }
    public int end(int start, String line) {
        return matcher.end(start, line.toLowerCase());
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
    public String[] getPattern() {
        return matcher.getPattern();
    }
    public void setPattern(String[] pattern) {
        for (int i = 0; i < pattern.length; ++i) {
            pattern[i] = pattern[i].toLowerCase();
        }
        this.matcher.setPattern(pattern);
    }
    public IgnoreCaseGrepMatcher withPattern(String[] pattern) {
        setPattern(pattern);
        return this;
    }

    // Attributes
    private GrepMatcher matcher;
}
