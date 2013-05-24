package com.dataiku.dip.input.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class AutoGZInputStream  {
    public static InputStream get(InputStream is, String filename) throws IOException {
        if (filename.endsWith(".gz")) {
            return new GZIPInputStream(is);
        } else {
            return is;
        }
    }
}
