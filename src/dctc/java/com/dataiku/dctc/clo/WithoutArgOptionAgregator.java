package com.dataiku.dctc.clo;

import java.util.ArrayList;
import java.util.List;

public class WithoutArgOptionAgregator implements OptionAgregator {
    public void addOpt(Option opt) {
        opts.add(opt);
    }
    public WithoutArgOptionAgregator withOpt(Option opt) {
        addOpt(opt);
        return this;
    }
    public List<Option> getOpts() {
        return opts;
    }
    public int count() {
        return count;
    }
    public int inc(String optName, int position) {
        int r = read(optName);
        if (r != 0) {
            ++count;
            this.position = position;
            return r;
        }

        return 0;
    }
    public int dec(String optName, int position) {
        int r = read(optName);
        if (r != 0) {
            --count;
            this.position = position;
            return r;
        }

        return 0;
    }
    public int has(String optName) {
        return read(optName);
    }
    private int read(String optName) {
        for (Option opt: opts) {
            int r = opt.read(optName);
            if (r != 0) {
                return r;
            }
        }
        return 0;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public WithoutArgOptionAgregator withDescription(String description) {
        setDescription(description);
        return this;
    }
    public int getPosition() {
        return position;
    }
    public boolean hasArgument() {
        return false;
    }
    public String getArgument() {
        return null;
    }
    public String getArgumentName() {
        return null;
    }

    // Attributes
    private int position;
    private String description;
    private int count;
    private List<Option> opts = new ArrayList<Option>();
}
