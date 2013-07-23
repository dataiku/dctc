package com.dataiku.dctc.clo;

import com.dataiku.dctc.command.policy.YellPolicy;

public interface Printer {
    public void start();
    public void newOption();
    public void add(char s);
    public void addParam(String p);
    public void add(String l);
    public void addDescription(String descrip);
    public void endOptionListing();
    public void endOption();
    public void description();
    public void synopsis(String cmdname, String syn);
    public void name(String cmdname, String tagline);
    public void print(YellPolicy yell);
}
