package com.dataiku.dctc.command;

import com.dataiku.dctc.file.GeneralizedFile;

interface GrepHeaderPrinter {
    public void print(GeneralizedFile file);
    public void forcePrint(GeneralizedFile file);
}
