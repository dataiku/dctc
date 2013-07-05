package com.dataiku.dctc.command.cat;

class CatByteSkip {
    // Getters - Settecs
    public long getSkip() {
        return skip;
    }
    public void setSkip(long skip) {
        this.skip = skip;
    }
    public CatByteSkip withSkip(long skip) {
        setSkip(skip);
        return this;
    }

    // Attributes
    private long skip;
}
