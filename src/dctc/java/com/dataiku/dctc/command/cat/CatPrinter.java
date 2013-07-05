package com.dataiku.dctc.command.cat;

public interface CatPrinter {
    public void print(String line);
    public void end();

    public CatHeader getHeader();
    public void setHeader(CatHeader header);
    public CatPrinter withHeader(CatHeader header);
    public EOLCatPrinter getEol();
    public void setEol(EOLCatPrinter eol);
    public CatPrinter withEol(EOLCatPrinter eol);
}
