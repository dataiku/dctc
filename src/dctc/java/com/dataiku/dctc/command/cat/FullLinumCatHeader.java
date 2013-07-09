package com.dataiku.dctc.command.cat;

class FullLinumCatHeader extends LinumCatHeader {
    public void print() {
        indent(getLineNumber(), "0");
        System.out.print(getLineNumber());
        System.out.print("  ");
        incr();
    }
}
