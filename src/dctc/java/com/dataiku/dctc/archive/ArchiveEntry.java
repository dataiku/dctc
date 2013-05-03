package com.dataiku.dctc.archive;

public interface ArchiveEntry {
    public String getName();
    public long getTime();
    public long getSize();
}
