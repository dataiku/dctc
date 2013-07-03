package com.dataiku.dctc.command;

interface GrepMatcher {
    public boolean match(String line);
    public int begin(String line);
    public int end(String line);
    public String getPattern();
    public void setPattern(String pattern);
    public GrepMatcher withPattern(String pattern);
}
