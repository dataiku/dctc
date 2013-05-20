package com.dataiku.dip.input.formats;


public class ExtractionLimit  {
    public ExtractionLimit() {
    }
    public ExtractionLimit(long maxRecords) {
        this.maxBytes = -1;
        this.maxRecords = maxRecords;
    }
    
    public long maxBytes = -1;
    public long maxRecords = -1;
}