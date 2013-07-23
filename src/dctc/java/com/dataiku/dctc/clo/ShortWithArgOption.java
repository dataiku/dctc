package com.dataiku.dctc.clo;

public class ShortWithArgOption implements Option {
    public String read(String optName) {
        if (opt.indexOf(optName.substring(0, 1)) != -1) {
            if (optName.length() > 1) {
                return optName.substring(1);
            }
            return null;
        }
        return "";
    }
    public String read(String optName, String argName) {
        System.err.println("debug: pipe");
        System.err.println("debug: opt: " + opt);
        if (opt.indexOf(optName.substring(0, 1)) != -1) {
             return argName;
        }
        return "";
    }
    public void print(Printer printer) {
        for (int i = 0; i < opt.length(); ++i) {
            printer.add(opt.charAt(i));
        }
    }
    public boolean print() {
        return true;
    }

    // Getters - Setters
    public String getOpt() {
        return opt;
    }
    public void setOpt(char opt) {
        this.opt = "" + opt;
    }
    public void setOpt(String opt) {
        this.opt += opt;
    }
    public ShortWithArgOption withOpt(String opt) {
        setOpt(opt);
        return this;
    }
    public ShortWithArgOption withOpt(char opt) {
        setOpt(opt);
        return this;
    }
    // Attributes
    private String opt = "";
}
