package com.dataiku.dctc.command.cat;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.dataiku.dctc.file.GeneralizedFile;

class BytesCatAlgorithm extends AbstractCatAlgorithm {
    public BytesCatAlgorithm(GeneralizedFile file) {
        super(file);
    }
    protected void _run(GeneralizedFile file) {
        InputStream i = open();

        { // Skip the beginning file.

            long skip = this.skip.getSkip();

            while (skip > 0) {
                try {
                    skip -= i.skip(skip);
                }
                catch (IOException e) {
                    yell("Failed to skip beginning bytes of " + file.givenName(),
                         e, 2);
                }
            }
        }

        try {
            IOUtils.copy(i, System.out);
        }
        catch (IOException e) {
            yell("Unexpected error while reading " + file.givenName(), e, 2);
        }
    }

    public CatByteSkip getSkip() {
        return skip;
    }
    public void setSkip(CatByteSkip skip) {
        this.skip = skip;
    }
    public BytesCatAlgorithm withSkip(CatByteSkip skip) {
        setSkip(skip);
        return this;
    }

    // Attributes
    private CatByteSkip skip;
}
