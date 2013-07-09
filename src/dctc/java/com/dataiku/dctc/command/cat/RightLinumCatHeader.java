package com.dataiku.dctc.command.cat;

class RightLinumCatHeader extends LinumCatHeader {
    public void print() {
        System.out.print(getLineNumber());
        incr();
    }
}
