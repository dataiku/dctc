package com.dataiku.dctc.command.grep;

import com.dataiku.dctc.file.GFile;

public interface GrepHeaderPrinter {
    public void print(GFile file);
    public void forcePrint(GFile file);
}
