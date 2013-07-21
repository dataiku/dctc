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
    public BasicEnrichedInputStream(InputStream in, long size, String desc) {
        this(in, size);
        this.desc = desc;
    }
    String desc = "InputStream";

    public boolean repeatable() {
        return false;
    }

    @Override
    public String desc() {
        return desc;
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
    @Override
    public InputStream headStream(long targetSize) throws IOException {
        return stream();
    }
}
