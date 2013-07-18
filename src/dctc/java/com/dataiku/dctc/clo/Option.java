package com.dataiku.dctc.clo;

import com.dataiku.dctc.clo.Printer;

public interface Option {
    public int read(String optName);
    public String getArgument(String optLine);
    public void print(Printer printer);
}
