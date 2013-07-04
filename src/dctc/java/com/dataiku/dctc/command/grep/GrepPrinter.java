package com.dataiku.dctc.command.grep;

import com.dataiku.dctc.file.GeneralizedFile;

interface GrepPrinter {
    public void print(String line);
    public void end(GeneralizedFile file);
}
