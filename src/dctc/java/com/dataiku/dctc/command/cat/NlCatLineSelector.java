package com.dataiku.dctc.command.cat;

class NlCatLineSelector implements CatLineSelector {
    public boolean needPrint(String line) {
        if (line.isEmpty()) {
            System.out.println();
            return false;
        }
        return true;
    }
}
