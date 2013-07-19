package com.dataiku.dctc.clo;

public class FJavaLongOption implements Option {
    public int read(String optName) {
        if (optName.equals(opt)) {
            return optName.length();
        }
        return 0;
    }
    public String getArgument(String optLine) {
        if (optLine.startsWith("=")) {
            return optLine.substring(1);
        }
        else {
            return null;
        }
    }
    public void print(Printer printer) {
        printer.add(opt);
    }

    // Getters - Setters
    public String getOpt() {
        return opt;
    }
    public void setOpt(String opt) {
        this.opt = opt;
    }
    public FJavaLongOption withOpt(String opt) {
        setOpt(opt);
        return this;
    }

    // Attributes
    private String opt;
}

