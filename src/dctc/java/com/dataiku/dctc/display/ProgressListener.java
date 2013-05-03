package com.dataiku.dctc.display;

public interface ProgressListener {
    public void inc(long size);
    public void size(long size);
}
