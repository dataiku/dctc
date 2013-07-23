package com.dataiku.dctc.copy;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.file.GFile;

public abstract class CopyTaskRunnable implements Runnable {
    protected CopyTaskRunnable(GFile input) {
        this.in = input;
        try {
            if (!input.isDirectory()) {
                try {
                    this.inSize = input.getSize();
                }
                catch (IOException e) {
                    this.inSize = -1;
                    exp = e;
                }
            }
            else {
                this.inSize = GlobalConstants.FOUR_KIO;
            }
        }
        catch (IOException e) {
            this.inSize = -1;
        }
    }
    public GFile getInputFile() {
        return in;
    }
    public synchronized boolean isStarted() {
        return started;
    }

    public synchronized boolean isDone() {
        return done;
    }

    public Exception getException() {
        return exp;
    }

    protected synchronized void inc(long size) {
        read += size;
    }
    public synchronized long read() {
        return read;
    }

    public void run() {
        synchronized (this) {
            started = true;
        }

        try {
            work();
        }
        catch (Exception e) {
            logger.info("Error in copy of " + getInputFile().getAbsoluteAddress(), e);
            exp = e;
        }
        finally {
            synchronized (this) {
                done = true;
            }
        }
    }
    public synchronized long getInSize() {
        return inSize;
    }

    public  abstract void work() throws IOException;
    public abstract String print() throws IOException;

    protected GFile in;
    private boolean started = false;
    private boolean done = false;
    private long read;
    private Exception exp;
    private long inSize;

    private static Logger logger = Logger.getLogger("dctc.copy");
}
