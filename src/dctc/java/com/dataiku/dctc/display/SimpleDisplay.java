package com.dataiku.dctc.display;


import java.util.List;

import com.dataiku.dctc.copy.CopyTaskRunnable;

public class SimpleDisplay extends AbstractThreadedDisplay {
    @Override
    protected final boolean display(CopyTaskRunnable task) {
        if (task.isDone()) {
            done += task.size();
            return true;
        } else {
            transfer += task.read();
            return false;
        }
    }
    @Override
    protected void resetLoop() {
        System.out.print("\rdone: " + Size.getReadableSize(done + transfer) + "/" + prettyFilesSize + "   ");
        transfer = 0;
    }
    @Override
    protected final void init(List<CopyTaskRunnable> taskList) {
        wholeFilesSize = 0;
        for (CopyTaskRunnable task: taskList) {
            wholeFilesSize += task.size();
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
}
