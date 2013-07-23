package com.dataiku.dctc.command.grep;

import com.dataiku.dctc.file.GFile;

public interface GrepPrinter {
    public void print(String line);
    public void end(GFile file);
}
