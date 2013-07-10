package com.dataiku.dctc.command.cat;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.file.GeneralizedFile;

class PartialFileTailAlgorithm extends AbstractCatAlgorithm {
    PartialFileTailAlgorithm(GeneralizedFile file) {
        super(file);
    }
    protected long _run(GeneralizedFile file) {
        long size; {
            try {
                size = file.getSize();
            }
            catch (IOException e) {
                yell("Unexpected error while getting the size of " + file.givenName(), e, 1);
                return -1;
            }
        }
        String line = "";

        while (size >= 0 && line.split("\n").length <= getNbLine()) {

            StringWriter writer = new StringWriter();
            try {
                size -= GlobalConstants.FIVE_MIO;
                IOUtils.copy(file.getRange(size, GlobalConstants.FIVE_MIO), writer, "UTF-8");
            } catch (IOException e) {
                yell("Unexpected error on " + file.givenName(), e, 1);
            }
            line = writer.toString() + line;
        }
        String[] lines = line.split("\n");
        for (int i = Math.max(0, lines.length - getNbLine()); i < lines.length; ++i) {
            System.out.println(lines[i]);
        }

        return Math.min(lines.length, nbLine);
    }

    // Getters - Setters
    public int getNbLine() {
        return nbLine;
    }
    public void setNbLine(long nbLine) {
        this.nbLine = (int) nbLine;
    }
    public PartialFileTailAlgorithm withNbLine(long nbLine) {
        setNbLine(nbLine);
        return this;
    }

    // Attributes
    private int nbLine;
}
