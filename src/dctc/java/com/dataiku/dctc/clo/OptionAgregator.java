package com.dataiku.dctc.clo;

import java.util.List;

public interface OptionAgregator {
    public String inc(String optName, int position);
    public String inc(String optName, String argName, int position);
    public String dec(String optName, int position);
    public String dec(String optName, String argName, int position);
    public boolean match(String optName);
    public int count();
    // Getters - Setters
    public void addOpt(Option opt);
    public OptionAgregator withOpt(Option opt);
    public List<Option> getOpts();
    public String getDescription();
    public int getPosition();
    public boolean hasArgument();
    public String getArgument();
    public String getArgumentName();
}
