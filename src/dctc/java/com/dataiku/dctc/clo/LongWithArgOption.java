package com.dataiku.dctc.clo;

import com.dataiku.dctc.file.FileManipulation;

public class LongWithArgOption implements Option {
    public String read(String optName) {
        if (optName.indexOf("=") != -1){
            String[/*opt=arg*/] optArg = FileManipulation.split(optName
                                                                , "="
                                                                , 2
                                                                , false);
            if (opt.equals(optArg[0])) {
                return optArg[1];
            }
            else {
                return "";
            }
        }
        else {
            return null;
        }

    }
    public String read(String optName, String argName) {
        if (optName.equals(opt)) {
            if (argName != null) {
                return argName;
            }
            else {
                return null; // Needs more arguments
            }
        }

        return ""; // Not for me.
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
    public LongWithArgOption withOpt(String opt) {
        setOpt(opt);
        return this;
    }

    // Attributes
    private String opt;
}

