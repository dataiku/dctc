package com.dataiku.dctc.command.cat;

class RightLinumCatHeader extends LinumCatHeader {
    public void print(String line) {
        System.out.print(getLineNumber());
        incr();
    }
}
