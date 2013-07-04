package com.dataiku.dctc.command.grep;

import com.dataiku.dctc.file.GeneralizedFile;

class QuietGrepHeaderPrinter implements GrepHeaderPrinter {
    QuietGrepHeaderPrinter(GrepHeaderPrinter header) {
        this.header = header;
    }
    public void print(GeneralizedFile file) {
    }
    public void forcePrint(GeneralizedFile file) {
        if (header != null) {
            header.forcePrint(file);
        }
    }

    private GrepHeaderPrinter header;
}
