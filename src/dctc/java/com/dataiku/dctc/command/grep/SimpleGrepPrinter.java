package com.dataiku.dctc.command.grep;

import com.dataiku.dctc.file.GFile;

class SimpleGrepPrinter implements GrepPrinter {
    public SimpleGrepPrinter() {
    }
    public void print(String line) {
        System.out.println(line);
    }
    public void end(GFile file) {
    }
}
