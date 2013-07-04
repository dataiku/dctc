package com.dataiku.dctc.command.cat;

class DollarEOLCatPrinter implements EOLCatPrinter {
    public void print() {
        System.out.println("$");
    }
}
