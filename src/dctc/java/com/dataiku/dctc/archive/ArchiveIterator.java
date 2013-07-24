package com.dataiku.dctc.archive;

import java.util.Iterator;

public interface ArchiveIterator extends Iterator<ArchiveEntry> {
    public boolean hasNext();
    public ArchiveEntry next();
    public void remove();
}
