package com.dataiku.dip.input.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.dataiku.dip.partitioning.Partition;

public interface EnrichedInputStream {
    /* Is it possible to get the stream several times ? */
    public boolean repeatable();
    public long size();
    public Map<String, String> metas();
    public Partition getPartition();
    public InputStream stream() throws IOException;
}
