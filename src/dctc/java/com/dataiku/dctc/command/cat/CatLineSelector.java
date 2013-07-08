package com.dataiku.dctc.command.cat;

interface CatLineSelector {
    public boolean needPrint(String line);
}
