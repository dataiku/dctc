package com.dataiku.dctc.command;

import com.dataiku.dctc.file.GeneralizedFile;

class CountGrepPrinter implements GrepPrinter {
    public CountGrepPrinter(GrepHeaderPrinter header) {
        this.header = header;
    }
    public void print(String line) {
        ++count;
    }
    public void end(GeneralizedFile file) {
        header.forcePrint(file);
        System.out.println(count);
    }

    private long count;
    private GrepHeaderPrinter header;
}
