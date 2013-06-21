package com.dataiku.dip.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;

public class StreamReplacementReader extends Reader {
    public StreamReplacementReader(StreamReplacement replace, Reader inputStream) {
        this.replace = replace;
        this.inputStream = inputStream;
    }
    @Override
    public int read() throws IOException {
        throw new IllegalArgumentException();
    }
    @Override
    public void close() throws IOException {
        inputStream.close();
    }
    @Override
    public void reset() {
        throw new Error("Not implemented.");
    }
    @Override
    public void mark(int readlimit) {
        throw new Error("Not implemented.");
    }
    @Override
    public boolean markSupported() {
        return false;
    }
    @Override
    public int read(CharBuffer cbuf) throws IOException {
        return 0;
    }
    @Override
    public int read(char[] cbuf) throws IOException {
        return read(cbuf, 0, cbuf.length);
    }
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (!buffer.isEmpty()) {
            char[] buffer = this.buffer.toCharArray();

            for (int i = 0; i < buffer.length && i < len; ++i) {
                cbuf[i + off] = buffer[i];
            }
            if (buffer.length > len) {
                char[] c = new char[buffer.length - len];
                for (int i = len; i < buffer.length; ++i) {
                    c[i - len] = buffer[i];
                }
                this.buffer = new String(c);
            }
            else {
            this.buffer = "";
            }
            int res = Math.min(len, buffer.length);
            if (res == 0) {
                return -1;
            }
            return res;
        }

        //int available = inputStream.available();
        int nbRead = inputStream.read(cbuf, off, len);
        if (nbRead == -1) {
            return -1;
        }
        String buffer = replace.transform(new String(cbuf, off, len), true);
        char[] res = buffer.toCharArray();
        for (int i = 0; i < len && i < res.length; ++i) {
            cbuf[i + off] = res[i];
        }
        if (len < res.length) {
            // We have cache
            char[] cache = new char[res.length - len];
            for (int i = len; i < res.length; ++i) {
                cache[i - len] = res[i];
            }
            this.buffer = new String(cache);
        }
        return Math.min(len, res.length);

    }
    @Override
    public boolean ready() {
        return false;
    }

    private StreamReplacement replace;
    private Reader inputStream;
    private String buffer = new String();

}
