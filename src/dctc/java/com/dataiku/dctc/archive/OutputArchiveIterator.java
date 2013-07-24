package com.dataiku.dctc.archive;

import java.io.IOException;

public abstract class OutputArchiveIterator implements ArchiveIterator {
    public abstract OutputArchiveEntry next();

    public boolean hasNext() {
        return true;
    }
    public void remove() {
    }
    public void close() throws IOException {
    }
}
