package com.dataiku.dctc.command.cat;

class LeftLinumCatHeader extends LinumCatHeader {
    public void print() {
        indent();
        System.out.print(getLineNumber());
        System.out.print("  ");
        incr();
    }

}
