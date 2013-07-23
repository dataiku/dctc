package com.dataiku.dctc.command.grep;

import com.dataiku.dctc.file.GFile;

class FileGrepPrinter implements GrepPrinter {
    public void print(String line) {
        match = true;
    }
    public void end(GFile file) {
        if (match) {
            System.out.println(file.givenName());
        }
    }

    // Attributes
    private boolean match = false;
}
