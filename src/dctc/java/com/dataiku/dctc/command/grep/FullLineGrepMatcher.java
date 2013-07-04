package com.dataiku.dctc.command.grep;

class FullLineGrepMatcher implements GrepMatcher {
    FullLineGrepMatcher(GrepMatcher matcher) {
        this.matcher = matcher;
    }

    public boolean match(String line) {
        return matcher.begin(line) == 0
            && matcher.end(0, line) == line.length();
    }

    public int begin(String line) {
        assert match(line)
            : "match(line)";
        return 0;
    }
    public int end(int start, String line) {
        assert match(line)
            : "match(line)";
        return line.length();
    }

    public String[] getPattern() {
        return matcher.getPattern();
    }
    public void setPattern(String[] pattern) {
        matcher.setPattern(pattern);
    }
    public FullLineGrepMatcher withPattern(String[] pattern) {
        setPattern(pattern);
        return this;
    }

    private GrepMatcher matcher;
}
