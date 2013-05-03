package com.dataiku.dctc.display;

import java.util.List;

import com.dataiku.dctc.copy.CopyTaskRunnable;

public class LessSimpleDisplay extends AbstractTransferRateDisplay {
    @Override
    protected final void init(List<CopyTaskRunnable> tasks) {
    }
    protected final void end() {
    }
    protected final void beginLoop(int taskSize) {
    }
    protected final void endLoop() {
        System.out.print(String.format("    \rdone: %s/%s - %d/%d files done - %sBps - %d transfer(s) running.",
                                       Size.getReadableSize(doneTransfer()),
                                       prettyWholeSize(),
                                       nbDone(),
                                       nbFiles(),
                                       Size.getReadableSize(getBnd()),
                                       nbRunning()));
    }
    protected final void done(CopyTaskRunnable task) {
    }
    protected final void fail(CopyTaskRunnable task) {
    }
    protected final void started(CopyTaskRunnable task) {
    }
    protected final void done() {
        System.out.println();
        System.out.println("Copy " + Size.getReadableSize(doneTransfer()) + " in "
                           + Date.getReadableDate(tick() / 10 + 1));
    }
}
