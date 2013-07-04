package com.dataiku.dctc.command.grep;

class InvGrepMatcher implements GrepMatcher {
    InvGrepMatcher(GrepMatcher matcher) {
        this.matcher = matcher;
    }

    public boolean match(String line) {
        return matcher.match(line);
    }
    public int begin(String line) {
        return -1; // FIXME: Maybe false, did we need to color the
                   // inverse?
    }
    public int end(int start, String line) {
        return 0; // Return the length of the matching group.
    }

    // Getters/Setters
    public GrepMatcher getMatcher() {
        return matcher;
    }
    public void setMatcher(GrepMatcher matcher) {
        this.matcher = matcher;
    }
    public InvGrepMatcher withMatcher(GrepMatcher matcher) {
        this.matcher = matcher;
        return this;
    }

    public String[] getPattern() {
        return matcher.getPattern();
    }
    public void setPattern(String[] pattern) {
        matcher.setPattern(pattern);
    }
    public InvGrepMatcher withPattern(String[] pattern) {
        setPattern(pattern);
        return this;
    }

    // Attributes
    private GrepMatcher matcher;
}
