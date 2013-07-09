package com.dataiku.dctc.command.cat;

abstract class LinumCatHeader implements CatHeader {
    public void indent() {
        for (int i = numberOfCol - ((int) Math.log10(lineNumber) + 1);
             i > 0;
             --i) {
            System.out.print(indentSeparator);
        }
    }
    public abstract void print();

    // Getters-Setters
    public final int getNumberOfCol() {
        return numberOfCol;
    }
    public final void setNumberOfCol(int numberOfCol) {
        this.numberOfCol = numberOfCol;
    }
    public final LinumCatHeader withNumberOfCol(int numberOfCol) {
        setNumberOfCol(numberOfCol);
        return this;
    }
    public long getLineNumber() {
        return lineNumber;
    }
    public void setLineNumber(long lineNumber) {
        this.lineNumber = lineNumber;
    }
    public LinumCatHeader withLineNumber(long lineNumber) {
        setLineNumber(lineNumber);
        return this;
    }
    public int getNumberIncrement() {
        return numberIncrement;
    }
    public void setNumberIncrement(int numberIncrement) {
        this.numberIncrement = numberIncrement;
    }
    public LinumCatHeader withNumberIncrement(int numberIncrement) {
        setNumberIncrement(numberIncrement);
        return this;
    }
    public void incr() {
        lineNumber += numberIncrement;
    }
    public String getIndentSeparator() {
        return indentSeparator;
    }
    public void setIndentSeparator(String indentSeparator) {
        this.indentSeparator = indentSeparator;
    }
    public LinumCatHeader withIndentSeparator(String indentSeparator) {
        setIndentSeparator(indentSeparator);
        return this;
    }

    // Attributes
    private String indentSeparator;
    private long lineNumber;
    private int numberIncrement;
    private int numberOfCol;
}
