package com.dataiku.dctc.clo;

import com.dataiku.dctc.clo.Printer;

public interface Option {
    public String read(String optName);
    public String read(String optName, String argName);
    public void print(Printer printer);
    public boolean print();
}
