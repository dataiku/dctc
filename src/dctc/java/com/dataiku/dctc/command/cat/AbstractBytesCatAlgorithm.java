package com.dataiku.dctc.command.cat;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.input.BoundedInputStream;

import com.dataiku.dctc.file.GFile;
import com.dataiku.dip.utils.StreamUtils;

abstract class AbstractBytesCatAlgorithm extends AbstractCatAlgorithm {
    public AbstractBytesCatAlgorithm(GFile file, String cmdname) {
        super(file, cmdname);
    }
    protected final long _run(GFile file) {
        InputStream i = open();
        if (i == null) {
            return -1;
        }
        i = bound(i);

        { // Skip the beginning file.

            try {
                StreamUtils.skip(i, skipFirst);
            }
            catch (IOException e) {
                yell("Failed to skip beginning bytes of " + file.givenName(),
                     e, 2);
            }
        }

        copy(i, file);
        return -1; // Line not counted, must not consider this return.

    }
    protected abstract void copy(InputStream inputStream, GFile file);
    // Getters Setters
    public long getSkipFirst() {
        return skipFirst;
    }
    public void setSkipFirst(long skipFirst) {
        this.skipFirst = skipFirst;
    }
    public AbstractBytesCatAlgorithm withSkipFirst(long skipFirst) {
        setSkipFirst(skipFirst);
        return this;
    }
    public long getSkipLast() {
        return skipLast;
    }
    public void setSkipLast(long skipLast) {
        this.skipLast = skipLast;
    }
    public AbstractBytesCatAlgorithm withSkipLast(long skipLast) {
        setSkipLast(skipLast);
        return this;
    }
    public InputStream bound(InputStream i) {
        return new BoundedInputStream(i, skipLast);
    }

    // Attributes
    private long skipFirst;
    private long skipLast = Long.MAX_VALUE;
}

