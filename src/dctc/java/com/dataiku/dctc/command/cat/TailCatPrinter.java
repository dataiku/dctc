package com.dataiku.dctc.command.cat;

public class TailCatPrinter extends AbstractCatPrinter {
    public void print(String line) {
        buf[idx] = line;
        idx = (idx + 1) % tail;
    }
    public void end() {
        for (int i = 0; i < tail; ++i) {
            getHeader().print();
            System.out.print(buf[(idx + i) % tail]);
            getEol().print();
        }
    }

    // Getters-Setters
    public int getTail() {
        return tail;
    }
    public void setTail(int tail) {
        this.tail = tail;
        buf = new String[tail];
        idx = 0;
    }
    public TailCatPrinter withTail(int tail) {
        setTail(tail);
        return this;
    }

    // Attributes
    private String[] buf;
    private int idx;
    private int tail;
}
