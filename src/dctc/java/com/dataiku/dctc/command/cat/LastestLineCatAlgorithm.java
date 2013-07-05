package com.dataiku.dctc.command.cat;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.dataiku.dctc.file.GeneralizedFile;

class LastestLineCatAlgorithm extends AbstractCatAlgorithm {
    public LastestLineCatAlgorithm(GeneralizedFile file) {
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

    public int getNbLine() {
        return nbLine;
    }
    public void setNbLine(int nbLine) {
        this.nbLine = nbLine;
    }
    public LastestLineCatAlgorithm withNbLine(int nbLine) {
        setNbLine(nbLine);
        return this;
    }

    private int nbLine;
}
