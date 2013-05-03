package com.dataiku.dctc.archive;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;

public class ZipInputArchiveIterator extends InputArchiveIterator {
    ZipInputArchiveIterator(ZipInputStream input) {
        this.input = input;
    }
    ZipInputArchiveIterator(InputStream input) {
        this.input = new ZipInputStream(input);
    }
    @Override
    public boolean hasNext() {
        try {
            return input.available() == 1;
        } catch (IOException e) {
            System.err.println("dctc ZipInputArchiveIterator: " + e.getMessage());
            return false;
        }
    }

    @Override
    public ZipInputArchiveEntry next() {
        try {
            ZipEntry entry = input.getNextEntry();
            if (entry == null) {
                return null;
            }
            return new ZipInputArchiveEntry(entry, input);
        } catch (IOException e) {
            System.err.println("dctc ZipInputArchiveIterator: " + e.getMessage());
            return null;
        }
    }

    private ZipInputStream input;
}
