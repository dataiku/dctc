package com.dataiku.dctc.archive;

import java.io.OutputStream;

public interface OutputArchiveEntry extends ArchiveEntry {
    public void create(String name);
    public String getName();
    public long getTime();
    public long getSize();

    public void setTime(long time);
    public OutputStream getContentStream();
    public void closeEntry();
}
