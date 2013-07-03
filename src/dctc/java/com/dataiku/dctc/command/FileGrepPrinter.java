package com.dataiku.dctc.command;

import com.dataiku.dctc.file.GeneralizedFile;

class FileGrepPrinter implements GrepPrinter {
    public void print(String line) {
        match = true;
    }
    public void end(GeneralizedFile file) {
        if (match) {
            System.out.println(file.givenName());
        }
    }

    // Attributes
    private boolean match = false;
}
