package com.dataiku.dctc.command.grep;

import com.dataiku.dctc.file.GFile;

class ColorFileGrepPrinter implements GrepPrinter {
    public void print(String line) {
        match = true;
    }
    public void end(GFile file) {
        if (match) {
            System.out.println("\u001B[1;35m" + file.givenName() + "\u001B[0m");
        }
    }

    // Attributes
    private boolean match = false;
}
