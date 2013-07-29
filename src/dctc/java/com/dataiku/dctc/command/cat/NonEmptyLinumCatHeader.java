package com.dataiku.dctc.command.cat;

class NonEmptyLinumCatHeader extends LinumCatHeader {
    public void print(String line) {
        if (!line.isEmpty()) {
            indent();
            System.out.print(getLineNumber());
            System.out.print("\t");
            incr();
        }
    }
}


