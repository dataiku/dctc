package com.dataiku.dctc.archive;

import java.io.InputStream;


import com.dataiku.dctc.GlobalConstants;

public class TarInputArchiveIterable extends InputArchiveIterable {
    public TarInputArchiveIterable(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public TarInputArchiveIterator iterator() {
        TarInputArchiveIterator res = new TarInputArchiveIterator(inputStream);
        inputStream = null;
        return res;
    }

    @Override
    public String getArchiveType() {
        return GlobalConstants.TAR;
    }

    private InputStream inputStream;
}
