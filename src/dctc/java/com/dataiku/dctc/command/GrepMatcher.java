package com.dataiku.dctc.command;

interface GrepMatcher {
    public boolean match(String line);
    public int idx(String line);
    public int len(String line);
    public String getPattern();
    public void setPattern(String pattern);
    public GrepMatcher withPattern(String pattern);
}
