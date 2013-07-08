package com.dataiku.dctc.command.cat;

import java.io.IOException;
import java.io.InputStream;

import com.dataiku.dip.utils.StreamUtils;

class CatByteSkip {
    // Getters - Setters
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

    public void skip(InputStream i) throws IOException {
        StreamUtils.skip(i, skip);
    }

    // Attributes
    private long skip;
}
