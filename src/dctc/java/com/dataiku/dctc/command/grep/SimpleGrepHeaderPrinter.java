package com.dataiku.dctc.command.grep;

import com.dataiku.dctc.file.GFile;

class SimpleGrepHeaderPrinter implements GrepHeaderPrinter {
    public void print(GFile file) {
        System.out.print(file.givenName());
        System.out.print(":");
    }
    public void forcePrint(GFile file) {
        print(file);
    }
}
