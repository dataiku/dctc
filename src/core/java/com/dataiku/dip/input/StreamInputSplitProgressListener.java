package com.dataiku.dip.input;

public class StreamInputSplitProgressListener extends InputSplitProgressListener {
    long readBytes;
    
    public synchronized void setReadBytes(long data) {
        readBytes = data;
    }
    public synchronized void incReadBytes(long data) {
        readBytes += data;
    }
    public synchronized long getReadBytes() {
        return readBytes;
    }
    
    public synchronized void setData(long readBytes, long readRecords, long errorRecords) {
        this.readBytes = readBytes;
        this.readRecords = readRecords;
        this.errorRecords = errorRecords;
    }
}