package com.dataiku.dctc.command.grep;

import com.dataiku.dctc.file.GFile;

class CountGrepPrinter implements GrepPrinter {
    public CountGrepPrinter(GrepHeaderPrinter header) {
        this.header = header;
    }
    public void print(String line) {
        ++count;
    }
    public void end(GFile file) {
        header.forcePrint(file);
        System.out.println(count);
    }

    private long count;
    private GrepHeaderPrinter header;
}
