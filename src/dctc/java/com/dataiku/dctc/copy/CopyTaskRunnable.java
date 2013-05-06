package com.dataiku.dctc.copy;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dctc.GlobalConstants;

public abstract class CopyTaskRunnable implements Runnable {
    protected CopyTaskRunnable(GeneralizedFile input) {
        this.in = input;
        try {
            if (!input.isDirectory()) {
                this.inSize = input.getSize();
            } else {
                this.inSize = GlobalConstants.FOUR_KIO;
            }
        } catch (IOException e) {
            this.inSize = -1;
        }
    }
    public GeneralizedFile getInputFile() {
        return in;
    }
    public boolean isStarted() {
        synchronized (this) {
            return started;
        }
    }

    public boolean isDone() {
        synchronized (done) {
            return done;
        }
    }

    public IOException getException() {
        return exp;
    }
    protected void inc(long size) {
        synchronized (read) {
            read += size;
        }
    }
    public long read() {
        return read;
    }

    public void run() {
        synchronized (started) {
            started = true;
        }

        try {
            work();
        } catch (IOException e) {
            logger.info("Error in copy of " + getInputFile().getAbsoluteAddress(), e);
            exp = e;
        }
        done = true;
    }
    public long getInSize() {
        return inSize;
    }

    public  abstract void work() throws IOException;
    public abstract String print() throws IOException;

    protected GeneralizedFile in;
    private Boolean started = false;
    private Boolean done = false;
    private Long read = 0l;
    private IOException exp;
    private long inSize;

    private static Logger logger = Logger.getLogger("dctc.copy");
}
