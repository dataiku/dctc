package com.dataiku.dctc.archive;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

public class TarInputArchiveIterator extends InputArchiveIterator {
    TarInputArchiveIterator(TarInputStream input) {
        this.input = input;
    }
    TarInputArchiveIterator(InputStream input) {
        this.input = new TarInputStream(input);
    }
    public boolean hasNext() {
        try {
            return input.available() == 1;
        }
        catch (IOException e) {
            System.err.println("dctc TarInputArchiveIterator: " + e.getMessage());
            return false;
        }
    }
    public InputArchiveEntry next() {
        try {
            TarEntry entry = input.getNextEntry();
            if (entry == null) {
                return null;
            }
            return new TarInputArchiveEntry(entry, input);
        }
        catch (IOException e) {
            System.err.println("dctc TarInputArchiveIterator: " + e.getMessage());
            return null;
        }
    }

    private TarInputStream input;
}
