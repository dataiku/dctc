package com.dataiku.dctc.command;

class StringGrepMatcher implements GrepMatcher {
    public StringGrepMatcher(String[] pattern) {
        this.pattern = pattern;
    }

    public boolean match(String line) {
        return begin(line) != -1;
    }
    public int begin(String line) {
        for (String pat: pattern) {
            int indx = line.indexOf(pat);
            if (indx != -1) {
                return indx;
            }
        }
        return -1;
    }
    public int end(String line) {
        for (String pat: pattern) {
            int indx = line.indexOf(pat);
            if (indx != -1) {
                return indx + pat.length();
            }
        }
        return -1;
    }

    // Getters/Setters
    public String[] getPattern() {
        return pattern;
    }
    public void setPattern(String[] pattern) {
        this.pattern = pattern;
    }
    public StringGrepMatcher withPattern(String[] pattern) {
        this.pattern = pattern;
        return this;
    }

    // Attributes
    private String[] pattern;
}
