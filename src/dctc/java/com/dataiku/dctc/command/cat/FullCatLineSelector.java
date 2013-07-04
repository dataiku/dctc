package com.dataiku.dctc.command.cat;

class FullCatLineSelector implements CatLineSelector {
    public boolean needPrint(String line) {
        return true;
    }
}
