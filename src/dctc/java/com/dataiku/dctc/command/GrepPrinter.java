package com.dataiku.dctc.command;

import com.dataiku.dctc.file.GeneralizedFile;

interface GrepPrinter {
    public void print(String line);
    public void end(GeneralizedFile file);
}
