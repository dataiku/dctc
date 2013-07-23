package com.dataiku.dctc.command.cat;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.dataiku.dctc.file.GFile;

class LatestLineCatAlgorithm extends AbstractCatAlgorithm {
    public LatestLineCatAlgorithm(GFile file, String cmdname) {
        super(file, cmdname);
    }
    protected long _run(GFile file) {
        try {
            IOUtils.copyLarge(file.getLastLines(getNbLine()), System.out);
        }
        catch (IOException e) {
            yell("Unexpected error on " + file.givenName(), e, 1);
        }

        return -1; // End of line not counted, should not consider
                   // this result.
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
