package com.dataiku.dctc.clo;

import com.dataiku.dctc.clo.Printer;

public class LongOption implements Option {
    public String read(String optName) {
        if (optName.equals(opt)) {
            return optName;
        }
        return "";
    }
    public String read(String optName, String argName) {
        return ""; // No arguments
    }
    public void print(Printer printer) {
        printer.add(opt);
    }
    public boolean print() {
        return true;
    }

    // Getters - Setters
    public String getOpt() {
        return opt;
    }
    public void setOpt(String opt) {
        this.opt = "-" + opt;
    }
    public LongOption withOpt(String opt) {
        setOpt(opt);
        return this;
    }

    // Attributes
    private String opt;
}
