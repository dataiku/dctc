package com.dataiku.dctc.command.cat;

class SimpleCatPrinter extends AbstractCatPrinter {
    public void print(String line) {
        getHeader().print();
        System.out.print(line);
        getEol().print();
    }
}
