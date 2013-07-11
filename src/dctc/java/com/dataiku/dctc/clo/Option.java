package com.dataiku.dctc.clo;

public class Option {
    public ShortOption getShortOption() {
        return shortOption;
    }
    public void setShortOption(ShortOption shortOption) {
        this.shortOption = shortOption;
    }
    public Option withShortOption(ShortOption shortOption) {
        setShortOption(shortOption);
        return this;
    }
    public LongOption getLongOption() {
        return longOption;
    }
    public void setLongOption(LongOption longOption) {
        this.longOption = longOption;
    }
    public Option withLongOption(LongOption longOption) {
        setLongOption(longOption);
        return this;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Option withDescription(String description) {
        setDescription(description);
        return this;
    }
    public int getCount() {
        return count;
    }
    public void inc() {
        ++count;
    }
    public void dec() {
        --count;
    }
    public boolean hasOption() {
        return hasOption;
    }
    public void setHasOption(boolean hasOption) {
        this.hasOption = hasOption;
    }
    public Option withHasOption(boolean hasOption) {
        setHasOption(hasOption);
        return this;
    }
    public String getArg() {
        return arg;
    }
    public void setArg(String arg) {
        if (!hasOption()) {
            throw new Error("Trying to set an argument to an option that doesn't have.");
        }
        this.arg = arg;
    }
    public Option withArg(String arg) {
        setArg(arg);
        return this;
    }
    public String getArgName() {
        return argName;
    }
    public void setArgName(String argName) {
        this.argName = argName;
    }
    public Option withArgName(String argName) {
        setArgName(argName);
        return this;
    }
    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }
    public Option withPosition(int position) {
        setPosition(position);
        return this;
    }

    // Attribute
    private int position;
    private String argName;
    private String arg;
    private boolean hasOption;
    private int count;
    private String description;
    private LongOption longOption;
    private ShortOption shortOption;
}
