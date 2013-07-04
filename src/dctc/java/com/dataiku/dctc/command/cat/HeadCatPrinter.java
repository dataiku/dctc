package com.dataiku.dctc.command.cat;

class HeadCatPrinter extends AbstractCatPrinter {
    public void print(String line) {
        if (buf[idx] != null) {
            getHeader().print();
            System.out.print(buf[idx]);
            getEol().print();
        }
        buf[idx] = line;

        idx = (idx + 1) % head;
    }

    public int getHead() {
        return head;
    }
    public void setHead(int head) {
        this.head = head;
        this.idx = 0;
        buf = new String[head];
    }
    public HeadCatPrinter withHead(int head) {
        setHead(head);
        return this;
    }

    // Attributes
    private int head;
    private int idx;
    private String[] buf;
}
