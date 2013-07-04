package com.dataiku.dctc.command.cat;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.dataiku.dctc.file.GeneralizedFile;

class SimpleCatAlgorithm extends AbstractCatAlgorithm {
    public SimpleCatAlgorithm(GeneralizedFile file) {
        super(file);
    }
    protected void _run(GeneralizedFile file) {
        InputStream i = open();
        try { // Print the file
            IOUtils.copyLarge(i, System.out);
            i.close();
        }
        catch (IOException e) {
            yell("Unexpected error while reading " + file.givenName(), e, 2);
            return;
        }
    }
}
