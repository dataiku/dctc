package com.dataiku.dctc.archive;

import java.io.InputStream;

import com.dataiku.dctc.GlobalConstants;

public class ZipInputArchiveIterable extends InputArchiveIterable {
    ZipInputArchiveIterable(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public ZipInputArchiveIterator iterator() {
        ZipInputArchiveIterator res = new ZipInputArchiveIterator(inputStream);
        inputStream = null;
        return res;
    }
    public String getArchiveType() {
        return GlobalConstants.ZIP;
    }

    private InputStream inputStream;
}
