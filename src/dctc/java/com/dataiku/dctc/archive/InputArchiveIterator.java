package com.dataiku.dctc.archive;

public abstract class InputArchiveIterator implements ArchiveIterator {
    public abstract boolean hasNext();
    public abstract InputArchiveEntry next();

    public void remove() {
    }
}
