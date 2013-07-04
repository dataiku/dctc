package com.dataiku.dctc.command.cat;

abstract class AbstractCatPrinter implements CatPrinter {
    public abstract void print(String line);

    // Getters - Setters
    public CatHeader getHeader() {
        return header;
    }
    public void setHeader(CatHeader header) {
        this.header = header;
    }
    public AbstractCatPrinter withHeader(CatHeader header) {
        setHeader(header);
        return this;
    }
    public EOLCatPrinter getEol() {
        return eol;
    }
    public void setEol(EOLCatPrinter eol) {
        this.eol = eol;
    }
    public AbstractCatPrinter withEol(EOLCatPrinter eol) {
        setEol(eol);
        return this;
    }

    // Attributes
    private EOLCatPrinter eol;
    private CatHeader header;
}
