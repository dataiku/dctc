package com.dataiku.dctc.command.cat;

class SkipFirstLine extends AbstractCatPrinter {
    public void print(String line) {
        if (skipNbLine <= 0) {
            printer.print(line);
        }
        else {
            --skipNbLine;
        }
    }
    public void end() {
        printer.end();
    }

    // Getters - Setters
    public long getSkipNbLine() {
        return skipNbLine;
    }
    public void setSkipNbLine(long skipNbLine) {
        this.skipNbLine = skipNbLine;
    }
    public SkipFirstLine withSkipNbLine(long skipNbLine) {
        setSkipNbLine(skipNbLine);
        return this;
    }
    public CatPrinter getPrinter() {
        return printer;
    }
    public void setPrinter(CatPrinter printer) {
        this.printer = printer;
    }
    public SkipFirstLine withPrinter(CatPrinter printer) {
        setPrinter(printer);
        return this;
    }

    // Attributes
    private CatPrinter printer;
    private long skipNbLine;
}
