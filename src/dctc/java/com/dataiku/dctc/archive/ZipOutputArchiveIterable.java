package com.dataiku.dctc.archive;

import java.io.OutputStream;

import com.dataiku.dctc.GlobalConstants;

public class ZipOutputArchiveIterable extends OutputArchiveIterable {
    public ZipOutputArchiveIterable(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
    @Override
    public String getArchiveType() {
        return GlobalConstants.ZIP;
    }

    @Override
    public ZipOutputArchiveIterator iterator() {
        ZipOutputArchiveIterator res = new ZipOutputArchiveIterator(outputStream);
        outputStream = null;
        return res;
    }

    private OutputStream outputStream;
}
