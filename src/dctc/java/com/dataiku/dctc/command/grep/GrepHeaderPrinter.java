package com.dataiku.dctc.command.grep;

import com.dataiku.dctc.file.GeneralizedFile;

interface GrepHeaderPrinter {
    public void print(GeneralizedFile file);
    public void forcePrint(GeneralizedFile file);
}
