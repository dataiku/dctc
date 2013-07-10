package com.dataiku.dctc.clo;

import java.util.ArrayList;
import java.util.List;

public class LongOption {
    public void addOpt(String opt) {
        if (isValid(opt)) {
            opts.add(opt);
        }
        else {
            throw new Error("Invalid Long Option: `" + opt + "'");
        }
    }
    public boolean isValid(String opt) {
        return opt.indexOf("-") != 0;
    }
    public boolean has(String optName) {
        for (String opt: opts) {
            if (opt.equals(optName)) {
                return true;
            }
        }

        return false;
    }
    public List<String> getOpts() {
        return opts;
    }

    // private
    List<String> opts = new ArrayList<String>();
}
