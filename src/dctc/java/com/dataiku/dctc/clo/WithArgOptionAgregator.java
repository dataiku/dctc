package com.dataiku.dctc.clo;

import java.util.ArrayList;
import java.util.List;

public class WithArgOptionAgregator implements OptionAgregator {
    public String inc(String optName, int position) {
        return read(optName, 1);
    }
    public String inc(String optName, String argName, int position) {
        String r = read(optName, argName, 1);
        if (r != null && !r.isEmpty()) {
            this.position = position;
            ++count;
        }
        return r;
    }
    public String dec(String optName, int position) {
        return read(optName, -1);
    }
    public String dec(String optName, String argName, int position) {
        String r = read(optName, argName, -1);
        if (r != null && !r.isEmpty()) {
            this.position = position;
            --count;
        }
        return r;
    }
    public int count() {
        return count;
    }
    public boolean match(String optName) {
        for (Option opt: opts) {
            String r = opt.read(optName);
            if (r == null || !r.isEmpty()) { // if needs more
                                             // arguments or has read
                                             // the optName
                return true;
            }
        }
        return false;
    }
    public String read(String optName, int inc) {
        for (Option opt: opts) {
            String r = opt.read(optName);
            if (r == null) {
                return null; // Needs more argument
            }
            if (r.isEmpty()) {
                continue;
            }
            setArgument(r);
            count += inc;

            return optName; // ok
        }

        return ""; // Not for me
    }
    public String read(String optName, String argName, int inc) {
        for (Option opt: opts) {
            String r = opt.read(optName, argName);
            if (!r.isEmpty()) {
                setArgument(r);
                count += inc;
                return r; // ok
            }
        }

        return ""; // Not for me
    }

    // Getters - Setter
    public void addOpt(Option opt) {
        opts.add(opt);
    }
    public WithArgOptionAgregator withOpt(Option opt) {
        addOpt(opt);
        return this;
    }
    public List<Option> getOpts() {
        return opts;
    }
    public boolean hasArgument() {
        return true;
    }
    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }
    public WithArgOptionAgregator withPosition(int position) {
        setPosition(position);
        return this;
    }
    public String getArgument() {
        return argument;
    }
    public void setArgument(String argument) {
        this.argument = argument;
    }
    public WithArgOptionAgregator withArgument(String argument) {
        setArgument(argument);
        return this;
    }
    public String getArgumentName() {
        return argumentName;
    }
    public void setArgumentName(String argumentName) {
        this.argumentName = argumentName;
    }
    public WithArgOptionAgregator withArgumentName(String argumentName) {
        setArgumentName(argumentName);
        return this;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public WithArgOptionAgregator withDescription(String description) {
        setDescription(description);
        return this;
    }

    // Attributes
    private int count;
    private String description;
    private String argumentName;
    private String argument;
    private int position;
    private List<Option> opts = new ArrayList<Option>();
}
