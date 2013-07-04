package com.dataiku.dctc.command.cat;

class ContinueCatStop implements CatStop {
    public boolean stop() {
        return false;
    }
}
