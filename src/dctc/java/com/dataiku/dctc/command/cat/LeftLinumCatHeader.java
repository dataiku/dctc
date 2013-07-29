package com.dataiku.dctc.command.cat;

class LeftLinumCatHeader extends LinumCatHeader {
    public void print(String line) {
        indent();
        System.out.print(getLineNumber());
        System.out.print("\t");
        incr();
    }

}
