package com.dataiku.dctc.command.cat;

import java.io.IOException;
import java.io.InputStream;

import com.dataiku.dctc.file.GFile;
import static com.dataiku.dctc.utils.CharUtils.showNonPrintable;

class PrettyBytesCatAlgorithm extends AbstractBytesCatAlgorithm {
    public PrettyBytesCatAlgorithm(GFile file, String cmdname) {
        super(file, cmdname);
    }
    protected void copy(InputStream inputStream, GFile file) {
        try {
            byte[] buf = new byte[1024];
            int nbRead;
            while ((nbRead = inputStream.read(buf)) != -1) {
                for (int j = 0; j < nbRead; ++j) {
                    showNonPrintable(System.out, buf[j], showTabulation);
                }
            }
        }
        catch (IOException e) {
            yell("Unexpected error while reading " + file.givenName(), e, 2);
        }
    }

    // Getters - Setters
    public boolean getShowTabulation() {
        return showTabulation;
    }
    public void setShowTabulation(boolean showTabulation) {
        this.showTabulation = showTabulation;
    }
    public PrettyBytesCatAlgorithm withShowTabulation(boolean showTabulation) {
        setShowTabulation(showTabulation);
        return this;
    }

    // Attributes
    private boolean showTabulation;
}
