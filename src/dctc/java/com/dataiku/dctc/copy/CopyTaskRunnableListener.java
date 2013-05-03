package com.dataiku.dctc.copy;

import com.dataiku.dctc.display.ProgressListener;

public class CopyTaskRunnableListener implements ProgressListener {
    public CopyTaskRunnableListener(CopyTaskRunnable copy) {
        this.copy = copy;
    }

    public void inc(long size) {
        synchronized(copy) {
            copy.inc(size);
        }
    }
    public void size(long size) {
        // Can not change the size of the input File.
    }

    private CopyTaskRunnable copy;
}
