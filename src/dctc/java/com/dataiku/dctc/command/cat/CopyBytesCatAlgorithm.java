package com.dataiku.dctc.command.cat;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.dataiku.dctc.file.GeneralizedFile;

class CopyBytesCatAlgorithm extends AbstractBytesCatAlgorithm {
    public CopyBytesCatAlgorithm(GeneralizedFile file, String cmdname) {
        super(file, cmdname);
    }
    protected void copy(InputStream inputStream, GeneralizedFile file) {
        try {
            IOUtils.copy(inputStream, System.out);
        }
        catch (IOException e) {
            yell("Unexpected error while reading " + file.givenName(), e, 2);
        }
    }
}
