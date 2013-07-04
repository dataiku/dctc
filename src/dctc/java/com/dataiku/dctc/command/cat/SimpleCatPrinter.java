package com.dataiku.dctc.command.cat;

class SimpleCatPrinter implements CatPrinter {
    public void print(String line) {
        System.out.print(line);
    }
}
