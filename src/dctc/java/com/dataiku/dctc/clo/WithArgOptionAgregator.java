package com.dataiku.dctc.clo;

import java.util.ArrayList;
import java.util.List;

import com.dataiku.dctc.file.PathManip;

public class WithArgOptionAgregator implements OptionAgregator {
    public int inc(String optName, int position) {
        int r = read(optName);

        if (r != 0) {
            this.position = position;
            ++count;
        }

        return r;
    }
    public int dec(String optName, int position) {
        int r = read(optName);

        if (r != 0) {
            this.position = position;
            --count;
        }

        return r;
    }
    public int count() {
        return count;
    }
    public int read(String optName) {
        String[] split = PathManip.split(optName, "=", 2);

        for (Option opt: opts) {
            int r = opt.read(split[0]);
            if (r != 0) {
                String arg = opt.getArgument(optName.substring(r));
                if (arg != null) {
                    setArgument(arg);
                    return optName.length();
                }
                else {
                    return 0;
                }
            }
        }

        return 0;
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
    public int has(String optName) {
        for (Option opt: opts) {
            int r = opt.read(optName);
            if (r != 0) {
                return r;
            }
        }
        return 0;
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
    public List<String> getArgument() {
        return argument;
    }
    public void setArgument(String argument) {
        this.argument.add(argument);
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
    private List<String> argument = new ArrayList<String>();
    private int position;
    private List<Option> opts = new ArrayList<Option>();
}
