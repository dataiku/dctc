package com.dataiku.dctc.command.cat;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.dataiku.dctc.file.GFile;

class CopyBytesCatAlgorithm extends AbstractBytesCatAlgorithm {
    public CopyBytesCatAlgorithm(GFile file, String cmdname) {
        super(file, cmdname);
    }
    protected void copy(InputStream inputStream, GFile file) {
        try {
            IOUtils.copy(inputStream, System.out);
        }
        catch (IOException e) {
            yell("Unexpected error while reading " + file.givenName(), e, 2);
        }
    }
}
