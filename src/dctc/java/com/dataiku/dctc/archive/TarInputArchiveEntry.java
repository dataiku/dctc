package com.dataiku.dctc.archive;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

public class TarInputArchiveEntry implements InputArchiveEntry {
    public TarInputArchiveEntry(TarEntry entry, TarInputStream stream) {
        this.entry = entry;
        this.stream = stream;
    }
    public String getName() {
        return entry.getName();
    }
    public InputStream getContentStream() {
        return stream;
    }
    public boolean isDirectory() {
        return entry.isDirectory();
    }
    public long getTime() {
        return entry.getModTime().getTime();
    }
    public long getSize() {
        return entry.getSize();
    }
    public long getCompressSize() {
        return getSize();
    }
    public boolean hasHash() {
        return false;
    }
    public String getHashMethod() {
        return "tar";
    }
    public String getHash() {
        return "tar";
    }
    public void closeEntry() throws IOException {
        //stream.close();

    }
    private TarEntry entry;
    private TarInputStream stream;
}
