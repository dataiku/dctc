package com.dataiku.dip.input;

public class StreamInputSplitProgressListener extends InputSplitProgressListener {
    long compressedBytes;
    long uncompressedBytes;
    
    public synchronized long getCompressedBytes() {
        return compressedBytes;
    }
    public synchronized long getUncompressedBytes() {
        return uncompressedBytes;
    }
    
    public synchronized void setData(long compressedBytes, long uncompressedBytes, long readRecords, long errorRecords) {
        this.compressedBytes = compressedBytes;
        this.uncompressedBytes = uncompressedBytes;
        this.readRecords = readRecords;
        this.errorRecords = errorRecords;
    }
}