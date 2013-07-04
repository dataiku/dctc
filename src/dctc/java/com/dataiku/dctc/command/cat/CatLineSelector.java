package com.dataiku.dctc.command.cat;

public interface CatLineSelector {
    public boolean needPrint(String line);
}
