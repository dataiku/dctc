package com.dataiku.dctc.display;

import java.util.List;

import com.dataiku.dctc.copy.CopyTaskRunnable;
import com.dataiku.dip.utils.FriendlyTime;

import static com.dataiku.dip.utils.PrettyString.scat;

class LessSimpleDisplay extends AbstractTransferRateDisplay {
    @Override
    protected final void init(List<CopyTaskRunnable> tasks) {
    }
    protected final void end() {
    }
    protected final void beginLoop(int taskSize) {
    }
    protected final void endLoop() {
        int fail = nbFail();
        String prettyFail = "";
        if (fail != 0) {
            prettyFail = String.format("(%d have failed) ", fail);
        }
        print(scat("done:"
                   , Size.getReadableSize(doneTransfer()), "/", prettyWholeSize()
                   , "in", FriendlyTime.elapsedTime(getElapsedTime() / 1000)
                   , nbDone(), "/", nbFiles(), "files done", prettyFail
                   , "-", Size.getReadableSize(getBnd()) + "Bps -"
                   , nbRunning(), "transfer(s) running."));
    }
    protected final void done(CopyTaskRunnable task) {
    }
    protected final void fail(CopyTaskRunnable task) {
    }
    protected final void started(CopyTaskRunnable task) {
    }
    protected final void done() {
        System.out.println();
        System.out.println(scat("Copied,"
                                , Size.getReadableSize(doneTransfer()),"in"
                                , FriendlyTime.elapsedTime(getTotalElapsedTime() / 1000)));
    }
}
