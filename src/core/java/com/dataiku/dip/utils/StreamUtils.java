package com.dataiku.dip.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class StreamUtils {
    public static final String defaultEncoding = "UTF-8";
    public static BufferedReader readStream(InputStream input,
                                            String encoding) throws UnsupportedEncodingException{
        return new BufferedReader(new InputStreamReader(input, encoding));
    }
    public static BufferedReader readStream(InputStream input) {
        try {
            return readStream(input, defaultEncoding);
        }
        catch (UnsupportedEncodingException e) {
            assert false;
            return null;
        }
    }
    public static BufferedReader readFile(File f,
                                          String encoding) throws FileNotFoundException,
                                                                  UnsupportedEncodingException {
        FileInputStream fis = new FileInputStream(f.getPath());
        InputStreamReader in = new InputStreamReader(fis, encoding);

        return new BufferedReader(in);
    }
    public static BufferedReader readFile(File f) throws FileNotFoundException {
        try {
            return readFile(f, defaultEncoding);
        }
        catch (UnsupportedEncodingException e) {
            assert false;
            return null;
        }
    }
    public static BufferedOutputStream readFD(FileDescriptor fd, String encoding) {
        return new BufferedOutputStream(new FileOutputStream(FileDescriptor.in));
    }
    public static BufferedOutputStream readFD(FileDescriptor fd) {
        return readFD(fd, defaultEncoding);
    }

    public static BufferedWriter writeToFile(File f,
                                             String encoding,
                                             boolean append) throws FileNotFoundException,
                                                                    UnsupportedEncodingException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, append), encoding));
    }
    public static BufferedWriter writeToFile(File f,
                                             boolean append) throws FileNotFoundException {
        try {
            return writeToFile(f, defaultEncoding, append);
        }
        catch (UnsupportedEncodingException e) {
            assert false;
            return null;
        }
    }

    public static void skip(InputStream i, long n) throws IOException {
        while (n > 0) {
            long skipped = i.skip(n);
            if (skipped == 0) {
                break;
            }
            n -= skipped;
        }
    }
}
