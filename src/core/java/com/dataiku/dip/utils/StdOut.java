package com.dataiku.dip.utils;

import java.io.PrintStream;
import java.util.Locale;

public class StdOut extends PrintStream {

    public StdOut(PrintStream out) {
        super(out);
        this.out = out;
    }
    @Override
    public void println() {
        sb.append(System.getProperty("line.separator"));
        flush();
    }
    @Override
    public void println(String msg) {
        sb.append(msg + System.getProperty("line.separator"));
        flush();
    }
    @Override
    public void print(String msg) {
        int index = msg.indexOf("\n");

        if (index != -1) {
            sb.append(msg);
            flush();
        } else {
            sb.append(msg);
        }
    }
    @Override
    public void flush() {
        out.print(sb.toString());
        out.flush();
        sb = new StringBuilder();
    }
    @Override
    public StdOut append(char c) {
        sb.append(c);
        flushIfNeeded();
        return this;
    }
    @Override
    public StdOut append(CharSequence csq) {
        sb.append(csq.toString());
        flushIfNeeded();
        return this;
    }
    @Override
    public StdOut append(CharSequence csq, int start, int end) {
        sb.append(csq.toString().substring(start, end));
        flushIfNeeded();
        return this;
    }
    @Override
    public boolean checkError() {
        return out.checkError();
    }
    @Override
    public void close() {
        flush();
        out.close();
    }
    @Override
    public StdOut format(Locale l, String format, Object... args) {
        sb.append(String.format(l, format, args));
        flushIfNeeded();
        return this;
    }
    @Override
    public StdOut format(String format, Object... args) {
        sb.append(String.format(format, args));
        flushIfNeeded();
        return this;
    }
    @Override
    public void print(boolean b) {
        sb.append(b);
    }
    @Override
    public void print(char c) {
        sb.append(c);
        flushIfNeeded();
    }
    @Override
    public void print(char[] s) {
        sb.append(s);
        flushIfNeeded();
    }
    @Override
    public void print(double d) {
        sb.append(d);
    }
    @Override
    public void print(float f) {
        sb.append(f);
    }
    @Override
    public void print(int i) {
        sb.append(i);
    }
    @Override
    public void print(long l) {
        sb.append(l);
    }
    @Override
    public void print(Object obj) {
        sb.append(obj);
        flushIfNeeded();
    }
    @Override
    public void println(boolean x) {
        print(x);
        println();
    }
    @Override
    public void println(char x) {
        print(x);
        println();
    }
    @Override
    public void println(char[] x) {
        print(x);
        println();
    }
    @Override
    public void println(double x) {
        print(x);
        println();
    }
    @Override
    public void println(float x) {
        print(x);
        println();
    }
    @Override
    public void println(int x) {
        print(x);
        println();
    }
    @Override
    public void println(long x) {
        print(x);
        println();
    }
    @Override
    public void println(Object x) {
        print(x);
        println();
    }
    @Override
    public void write(byte[] buf, int off, int len) {
        boolean flush = false;
        for (int i = 0; i < len; ++i) {
            sb.append((char) buf[i + off]);
            flush = flush || buf[i + off] == '\n';
        }
        if (flush) {
            flush();
        }
    }
    @Override
    public void write(int b) {
        sb.append((char) b);
        flushIfNeeded();
    }
    public void flushIfNeeded() {
        if (sb.toString().indexOf(System.getProperty("line.separator")) != -1) {
            flush();
        }
    }

    // Attributes
    private StringBuilder sb = new StringBuilder();
    private PrintStream out;
}
