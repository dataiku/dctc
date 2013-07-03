package com.dataiku.dctc.command;

class StringGrepMatcher implements GrepMatcher {
    public StringGrepMatcher(String[] pattern) {
        this.pattern = pattern;
    }

    public boolean match(String line) {
        for (String pat: pattern) {
            if (line.indexOf(pat) != -1)
                return true;
        }
        return false;
    }
    public int begin(String line) {
        assert match(line)
            : "match(line)";

        int res = Integer.MAX_VALUE;

        for (String pat: pattern) {
            int indx = line.indexOf(pat);
            if (indx != -1) {
                res = Math.min(res, indx);
            }
        }
        return res;
    }
    public int end(int start, String line) {
        assert begin(line) == start
            : "begin(line) == start";

        int res = start;
        for (String pat: pattern) {
            int indx = line.indexOf(pat);
            if (indx == start) {
                res = Math.max(res, indx + pat.length());
            }
        }
        return res;
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
