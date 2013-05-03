package com.dataiku.dctc.archive;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipOutputArchiveEntry implements OutputArchiveEntry {
    public ZipOutputArchiveEntry(ZipOutputStream stream) {
        this.stream = stream;
    }

    @Override
    public void create(String name) {
        this.entry = new ZipEntry(name);
        try {
            stream.putNextEntry(entry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return entry.getName();
    }

    @Override
    public long getTime() {
        return entry.getTime();
    }

    @Override
    public long getSize() {
        return entry.getSize();
    }

    @Override
    public void setTime(long time) {
        entry.setTime(time);
    }

    @Override
    public OutputStream getContentStream() {
        return stream;
    }

    @Override
    public void closeEntry() {
        try {
            stream.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ZipEntry entry;
    private ZipOutputStream stream;
}
