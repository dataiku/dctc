package com.dataiku.dctc.command.grep;

public interface GrepMatcher {
    public boolean match(String line);
    public int begin(String line);
    public int end(int begin, String line);
    public String[] getPattern();
    public void setPattern(String[] pattern);
    public GrepMatcher withPattern(String[] pattern);
}
