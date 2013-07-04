package com.dataiku.dctc.command.grep;

class ColoredGrepLinePrinter implements GrepLinePrinter {
    public void print(long line) {
        System.out.print("\u001B[1;32m" + line + "\u001B[1;36m:\u001B[0m");
    }
}
