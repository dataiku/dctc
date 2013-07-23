package com.dataiku.dctc.clo;

public class FJavaLongOption implements Option {
    public String read(String optName) {
        if (optName.equals(opt)) {
            return null;
        }
        return "";
    }
    public String read(String optName, String argName) {
        if (optName.equals(opt)) {
            return argName;
        }
        return "";
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
    public boolean print() {
        return true;
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
