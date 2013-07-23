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
    public String inc(String optName, int position) {
        return read(optName, 1, position);
    }
    public String inc(String optName, String argName, int position) {
        throw new Error("Never reached.");
    }
    public String  dec(String optName, int position) {
        return read(optName, -1, position);
    }
    public String dec(String optName, String argName, int position) {
        throw new Error("Never reached");
    }
    public boolean match(String optName) {
        for (Option opt: opts) {
            String r = opt.read(optName);
            if (r == null || !r.isEmpty()) {
                return true;
            }
        }
        return false;
    }
    public String read(String optName, int inc, int position) {
        for (Option opt: opts) {
            String r = opt.read(optName);
            if (r.isEmpty()) {
                continue;
            }
            count += inc;
            this.position = position;
            return r; // ok
        }

        return ""; // Not for me
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
