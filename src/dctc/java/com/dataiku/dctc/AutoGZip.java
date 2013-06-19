package com.dataiku.dctc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

import com.dataiku.dctc.file.FileManipulation;
import com.dataiku.dctc.file.GeneralizedFile;

public class AutoGZip {
    public static InputStream buildInput(String fileName, InputStream stream) throws IOException {
        if (fileName.endsWith(".gz")) {
            return new GZIPInputStream(stream);
        }
        else if (fileName.endsWith(".gz2")) {
            return new BZip2CompressorInputStream(stream);
        }

        else {
            return stream;
        }
    }

    public static InputStream buildInput(GeneralizedFile g) throws IOException {
        try {
            return buildInput(g.getFileName(), g.inputStream());
        }
        catch (IOException e) {
            if (g.exists()) {
                return g.inputStream();
            } else {
                throw e;
            }
        }
    }
    public static OutputStream buildOutput(String fileName, OutputStream stream) throws IOException {
        if (fileName.endsWith(".gz")) {
            return new GZIPOutputStream(stream);
        }
        else if (fileName.endsWith(".gz2")) {
            return new BZip2CompressorOutputStream(stream);
        }

        else {
            return stream;
        }
    }
    public static OutputStream buildOutput(GeneralizedFile g) throws IOException {
        assert g.hasOutputStream();
        try {
            return buildOutput(g.getFileName(), g.outputStream());
        }
        catch (IOException e) {
            if (g.exists()){
                return g.outputStream();
            }
            else {
                throw e;
            }
        }
    }
}
