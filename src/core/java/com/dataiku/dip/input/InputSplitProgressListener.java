package com.dataiku.dip.input;

public class InputSplitProgressListener {
    long readRecords;
    long errorRecords;
     
    public synchronized void setReadRecords(long data) {
        readRecords = data;
    }
    public synchronized void incReadRecords(long data) {
        readRecords += data;
    }
    public synchronized long getReadRecords() {
        return readRecords;
    }
    public synchronized void setErrorRecords(long data) {
        errorRecords = data;
    }
    public synchronized void incErrorRecords(long data) {
        errorRecords += data;
    }
    public synchronized long getErrorRecords() {
        return errorRecords;
    }
}
