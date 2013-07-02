package com.dataiku.dctc.command;

import com.dataiku.dctc.file.GeneralizedFile;

class SimpleGrepHeaderPrinter implements GrepHeaderPrinter {
    public void print(GeneralizedFile file) {
        System.out.print(file.givenName());
        System.out.print(":");
    }
    public void forcePrint(GeneralizedFile file) {
        print(file);
    }
}
