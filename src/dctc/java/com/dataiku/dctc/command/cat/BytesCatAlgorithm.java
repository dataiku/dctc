package com.dataiku.dctc.command.cat;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dip.utils.StreamUtils;
import org.apache.commons.io.input.BoundedInputStream;

class BytesCatAlgorithm extends AbstractCatAlgorithm {
    public BytesCatAlgorithm(GeneralizedFile file) {
        super(file);
    }
    protected long _run(GeneralizedFile file) {
        InputStream i = bound(open());

        { // Skip the beginning file.

            try {
                StreamUtils.skip(i, skipFirst);
            }
            catch (IOException e) {
                yell("Failed to skip beginning bytes of " + file.givenName(),
                     e, 2);
            }
        }

        try {
            IOUtils.copy(i, System.out);
        }
        catch (IOException e) {
            yell("Unexpected error while reading " + file.givenName(), e, 2);
        }

        return -1; // Line not counted, must not consider this return.
    }

    // Getters-Setters
    public long getSkipLast() {
        return skipLast;
    }
    public void setSkipLast(long skipLast) {
        this.skipLast = skipLast;
    }
    public BytesCatAlgorithm withSkipLast(long skipLast) {
        setSkipLast(skipLast);
        return this;
    }
    public long getSkipFirst() {
        return skipFirst;
    }
    public void setSkipFirst(long skipFirst) {
        this.skipFirst = skipFirst;
    }
    public BytesCatAlgorithm withSkipFirst(long skipFirst) {
        setSkipFirst(skipFirst);
        return this;
    }
    public InputStream bound(InputStream i) {
        return new BoundedInputStream(i, skipLast);
    }

    // Attributes
    private long skipFirst;
    private long skipLast = Long.MAX_VALUE;
}
