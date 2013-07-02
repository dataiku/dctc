package com.dataiku.dctc.command;

import com.dataiku.dctc.file.GeneralizedFile;

class SimpleGrepPrinter implements GrepPrinter {
    public SimpleGrepPrinter() {
    }
    public void print(String line) {
        System.out.println(line);
    }
    public void end(GeneralizedFile file) {
    }
}
