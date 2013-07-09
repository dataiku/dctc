package com.dataiku.dctc.clo;

public class ShortOption {
    public void addOpt(char opt) {
        opts += opt;
    }
    public void addOpts(String opt) {
        opts += opt;
    }
    public String getOpts() {
        return opts;
    }
    public boolean has(char opt) {
        return opts.indexOf(opt) != -1;
    }

    // private
    private String opts = "";
}
