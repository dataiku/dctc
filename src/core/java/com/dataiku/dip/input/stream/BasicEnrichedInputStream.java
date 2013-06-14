package com.dataiku.dip.input.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.dataiku.dip.partitioning.Partition;

public class BasicEnrichedInputStream implements EnrichedInputStream {
    public BasicEnrichedInputStream(InputStream in, long size) {
        this.in = in;
        this.size = size;
    }
    public boolean repeatable() {
        return false;
    }
    public long size() {
        return size;
    }
    public Map<String, String> metas() {
        return new HashMap<String, String>();
    }
    public InputStream stream() throws IOException {
        return in;
    }

    private InputStream in;
    private long size;

    @Override
    public Partition getPartition() {
        return null;
    }
}
