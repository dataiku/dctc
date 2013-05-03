package com.dataiku.dctc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.dataiku.dctc.file.FileManipulation;
import com.dataiku.dctc.file.GeneralizedFile;

public class AutoGZip {
    public static InputStream buildInput(GeneralizedFile g) throws IOException {
        if (FileManipulation.extension(g.getFileName(), ".").equals("gz")) {
            try {
                return new GZIPInputStream(g.inputStream());
            } catch (IOException e) {
                if (g.exists()) {
                    return g.inputStream();
                } else {
                    throw e;
                }
            }
        } else {
            return g.inputStream();
        }
    }
    public static OutputStream buildOutput(GeneralizedFile g) throws IOException {
        assert g.hasOutputStream();
        if (g.getFileName().endsWith(".gz")) {
            return new GZIPOutputStream(g.outputStream());
        } else {
            return g.outputStream();
        }
    }
}
