package com.dataiku.dctc.command.cat;

class HeadCatStop implements CatStop {
    public boolean stop() {
        return head-- <= 0;
    }
    public long getHead() {
        return head;
    }
    public void setHead(long head) {
        this.head = head;
    }
    public HeadCatStop withHead(long head) {
        setHead(head);
        return this;
    }

    // Attributes
    private long head;
}
