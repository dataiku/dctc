package com.dataiku.dctc.command;

class OnGrepLinePrinter implements GrepLinePrinter {
    public void print(long line) {
        System.out.print(line);
        System.out.print(":");
    }
}
