package com.dataiku.dctc.command.cat;

class LeftLinumCatHeader extends LinumCatHeader {
    public void print() {
        indent(getLineNumber());
        System.out.print(getLineNumber());
        System.out.print("  ");
        incr();
    }

}
