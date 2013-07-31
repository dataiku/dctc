package com.dataiku.dctc.display;


import java.util.List;

import com.dataiku.dctc.copy.CopyTaskRunnable;

class SimpleDisplay extends AbstractThreadedDisplay {
    @Override
    protected final boolean display(CopyTaskRunnable task) {
        if (task.isDone()) {
            if (task.getException() != null) {
                ++failed;
            }
            done += task.getInSize();
            return true;
        } else {
            transfer += task.read();
            return false;
        }
    }
    @Override
    protected void resetLoop() {
        String msg = "done: "
            + Size.getReadableSize(done + transfer)
            + "/"
            + prettyFilesSize;
        if (failed != 0) {
            msg += " with " + failed + " fail(s).";
        } else {
            msg += ".";
        }
        print(msg);
        transfer = 0;
    }
    @Override
    protected final void init(List<CopyTaskRunnable> taskList) {
        wholeFilesSize = 0;
        for (CopyTaskRunnable task: taskList) {
            wholeFilesSize += task.getInSize();
        }
        prettyFilesSize = Size.getReadableSize(wholeFilesSize);
    }
    @Override
    protected final void end() {
        System.out.println();
    }

    // Attributes
    private long wholeFilesSize;
    private String prettyFilesSize;
    private long done;
    private long transfer;
    private int failed;
}
