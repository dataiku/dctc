package com.dataiku.dctc.archive;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipOutputArchiveEntry implements OutputArchiveEntry {
    public ZipOutputArchiveEntry(ZipOutputStream stream) {
        this.stream = stream;
    }

    public void create(String name) throws IOException {
        this.entry = new ZipEntry(name);
        stream.putNextEntry(entry);
    }
    public String getName() {
        return entry.getName();
    }
    public long getTime() {
        return entry.getTime();
    }
    public long getSize() {
        return entry.getSize();
    }
    public void setTime(long time) {
        entry.setTime(time);
    }
    public OutputStream getContentStream() {
        return stream;
    }
    public void closeEntry() throws IOException {
        stream.closeEntry();
    }

    private ZipEntry entry;
    private ZipOutputStream stream;
}
