package com.dataiku.dctc.clo;

import com.dataiku.dip.utils.IntegerUtils;

public class NumberOption implements Option {
    public String read(String optName) {
        if (IntegerUtils.isNumeric(optName)) {
            return optName;
        }
        return "";
    }
    public String read(String optName, String argName) {
        return "";
    }
    public void print(Printer printer) {
        printer.add(argName);
    }
    public boolean print() {
        return false;
    }

    // Getters - Setters
    public String getArgName() {
        return argName;
    }
    public void setArgName(String argName) {
        this.argName = argName;
    }
    public NumberOption withArgName(String argName) {
        setArgName(argName);
        return this;
    }

    // Attributes
    private String argName;
}
