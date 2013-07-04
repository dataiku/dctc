package com.dataiku.dctc.command.cat;

class NewLineEOLCatPrinter implements EOLCatPrinter {
    public void print() {
        System.out.println();
    }
}
