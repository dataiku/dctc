package com.dataiku.dctc.command.cat;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.dataiku.dctc.file.GeneralizedFile;

class LatestLineCatAlgorithm extends AbstractCatAlgorithm {
    public LatestLineCatAlgorithm(GeneralizedFile file) {
        super(file);
    }
    protected void _run(GeneralizedFile file) {
        try {
            IOUtils.copyLarge(file.getLastLines(getNbLine()), System.out);
        }
        catch (IOException e) {
            yell("Unexpected error on " + file.givenName(), e, 1);
        }
    }

    public long getNbLine() {
        return nbLine;
    }
    public void setNbLine(long nbLine) {
        this.nbLine = nbLine;
    }
    public LatestLineCatAlgorithm withNbLine(long nbLine) {
        setNbLine(nbLine);
        return this;
    }

    private long nbLine;
}
