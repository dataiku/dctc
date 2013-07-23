package com.dataiku.dctc.command.grep;

import com.dataiku.dctc.file.GFile;

class QuietGrepHeaderPrinter implements GrepHeaderPrinter {
    QuietGrepHeaderPrinter(GrepHeaderPrinter header) {
        this.header = header;
    }
    public void print(GFile file) {
    }
    public void forcePrint(GFile file) {
        if (header != null) {
            header.forcePrint(file);
        }
    }

    private GrepHeaderPrinter header;
}
