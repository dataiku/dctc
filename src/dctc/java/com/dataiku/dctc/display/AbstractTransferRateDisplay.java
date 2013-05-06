package com.dataiku.dctc.display;

import java.util.ArrayList;
import java.util.List;

import com.dataiku.dctc.copy.CopyTaskRunnable;

abstract public class AbstractTransferRateDisplay implements ThreadedDisplay {
    public final List<CopyTaskRunnable> work(List<CopyTaskRunnable> tasks) {
        // Init
        nbFiles = tasks.size();
        init(tasks);
        calculateSize(tasks);
        initVar();

        List<CopyTaskRunnable> errorList = new ArrayList<CopyTaskRunnable>();

        while (!tasks.isEmpty()) {
            // Re-initialize needed variables
            transfer = 0;
            nbRunning = 0;

            beginLoop(tasks.size());

            for (int i = 0; i < tasks.size(); ++i) {
                CopyTaskRunnable elt = tasks.get(i);
                synchronized (elt) {
                    if (elt.isDone()) {
                        ++nbDone;
                        if (elt.getException() != null) {
                            ++nbFail;
                            errorList.add(elt);
                            fail(elt);
                        } else {
                            done(elt);
                        }
                        done += elt.getInSize();
                        tasks.remove(i);
                    } else {
                        if (elt.isStarted()) {
                            ++nbRunning;
                            started(elt);
                            transfer += elt.read();
                        }
                    }
                }
            }
            bandWidth[cursorPos()] = done + transfer;
            if (cursorPos() == 0) {
                bndWdth = moyBndWdth / 10;
                moyBndWdth = 0;
            }
            moyBndWdth += (bandWidth[cursorPos()] - bandWidth[(cursorPos() + 1) % 10]);

            ++tick;
            endLoop();
            sleep(100);
        }
        done();
        return errorList;
    }

    protected abstract void beginLoop(int taskSize);
    protected abstract void endLoop();
    protected abstract void init(List<CopyTaskRunnable> tasks);
    protected abstract void done(CopyTaskRunnable task);
    protected abstract void fail(CopyTaskRunnable task);
    protected abstract void started(CopyTaskRunnable task);
    protected abstract void done();

    protected long wholeSize() {
        return wholeSize;
    }
    protected String prettyWholeSize() {
        return prettyWholeSize;
    }
    protected int cursorPos() {
        return tick % 10;
    }
    protected int tick() {
        return tick;
    }
    protected long getBnd() {
        return bndWdth;
    }
    protected long doneTransfer() {
        return done + transfer;
    }
    protected void sleep(int millisecond) {
        try {
            Thread.sleep(millisecond);
        } catch (InterruptedException e) {
        }
    }
    protected int nbDone() {
        return nbDone;
    }
    protected int nbRunning() {
        return nbRunning;
    }
    protected int nbFail() {
        return nbFail;
    }
    protected int nbFiles() {
        return nbFiles;
    }

    private void initVar() {
        bandWidth = new long[10];
    }
    private void calculateSize(List<CopyTaskRunnable> tasks) {
        wholeSize = 0;
        for (CopyTaskRunnable task: tasks) {
            wholeSize += task.getInSize();
        }
        prettyWholeSize = Size.getReadableSize(wholeSize);
    }
    protected void print(String msg) {
        for (int i = lastLength - msg.length(); i >= 0; --i) {
            System.out.print(" ");
        }
        System.out.print("\r");
        System.out.print(msg);
        lastLength = msg.length();
    }
    private int lastLength;

    // Attributes
    /// File sizes
    private long wholeSize;
    private String prettyWholeSize;
    /// Band Width.
    private long[] bandWidth;
    private long moyBndWdth;
    private long bndWdth;
    private int tick;
    /// Size transfered
    private long done;
    private long transfer;
    private int nbDone;
    private int nbRunning;
    private int nbFail;
    private int nbFiles;
}
