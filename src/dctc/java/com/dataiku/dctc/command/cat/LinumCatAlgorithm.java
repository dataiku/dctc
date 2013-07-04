package com.dataiku.dctc.command.cat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dip.utils.StreamUtils;

class LinumCatAlgorithm extends AbstractCatAlgorithm {
    LinumCatAlgorithm(GeneralizedFile file) {
        super(file);
    }

    protected void _run(GeneralizedFile file) {

        InputStream in = open();
        if (in == null) {
            return;
        }

        BufferedReader reader = StreamUtils.readStream(in);
        String line;
        try {
            while (!stop.stop() && ((line = reader.readLine()) != null)) {
                if (select.needPrint(line)) {
                    printer.print(line);
                }
            }
        }
        catch (IOException e) {
            yell("Unexpected error", e, 2);
        }
    }

    public CatLineSelector getSelect() {
        return select;
    }
    public void setSelect(CatLineSelector select) {
        this.select = select;
    }
    public LinumCatAlgorithm withSelect(CatLineSelector select) {
        setSelect(select);
        return this;
    }
    public CatPrinter getPrinter() {
        return printer;
    }
    public void setPrinter(CatPrinter printer) {
        this.printer = printer;
    }
    public LinumCatAlgorithm withPrinter(CatPrinter printer) {
        setPrinter(printer);
        return this;
    }
    public CatStop getStop() {
        return stop;
    }
    public void setStop(CatStop stop) {
        this.stop = stop;
    }
    public LinumCatAlgorithm withStop(CatStop stop) {
        setStop(stop);
        return this;
    }

    // Attributes
    private CatLineSelector select;
    private CatPrinter printer;
    private CatStop stop;
}
