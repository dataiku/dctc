package com.dataiku.dctc.command.cat;

abstract class LinumCatHeader implements CatHeader {
    public void indent(int indentSize, String sep) {
        for (int i = 0; i < indentSize; ++i) {
            System.out.print(sep);
        }
    }
    public void indent(int indentSize) {
        indent(indentSize, " ");
    }
    public void indent(long number) {
        indent(number, " ");
        indent(numberOfCol - (int) Math.log10(number) + 1);
    }
    public void indent(long number, String sep) {
        indent(numberOfCol - (int) Math.log10(number) + 1, sep);
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

    // Attributes
    private long lineNumber = 1;
    private int numberIncrement = 1;
    private int numberOfCol = 6;
}
