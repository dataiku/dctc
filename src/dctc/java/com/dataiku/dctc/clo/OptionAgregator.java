package com.dataiku.dctc.clo;

import java.util.List;

public interface OptionAgregator {
    public int inc(String optName, int position);
    public int dec(String optName, int position);
    public int count();
    // Getters - Setters
    public void addOpt(Option opt);
    public OptionAgregator withOpt(Option opt);
    public List<Option> getOpts();
    public String getDescription();
    public int has(String optName);
    public int getPosition();
    public boolean hasArgument();
    public List<String> getArgument();
    public String getArgumentName();
}
