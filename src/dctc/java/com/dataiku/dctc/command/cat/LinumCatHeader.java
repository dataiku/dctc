package com.dataiku.dctc.command.cat;

class LinumCatHeader implements CatHeader {
    public void print() {
        ++count;
        for (long i = (long) Math.log10(count) + 1; i < numberOfCol; ++i) {
            System.out.print(" ");
        }
        System.out.print(count);
        System.out.print("  ");

    }

    // Getters-Setters
    public long getNumberOfCol() {
        return numberOfCol;
    }
    public void setNumberOfCol(long numberOfCol) {
        this.numberOfCol = numberOfCol;
    }
    public LinumCatHeader withNumberOfCol(long numberOfCol) {
        setNumberOfCol(numberOfCol);
        return this;
    }

    // Attributes
    private long numberOfCol = 6;
    private long count;
}
