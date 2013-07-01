package com.dataiku.dip.input.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.dataiku.dip.partitioning.Partition;

public interface EnrichedInputStream {
    /* Is it possible to get the stream several times ? */
    public boolean repeatable();
    public long size() throws IOException;
    public Map<String, String> metas();
    public Partition getPartition();
    public InputStream stream() throws IOException;
    
    /**
     * Returns a stream that *might* be limited to the first "targetSize" bytes of the stream.
     * Note that there is no guarantee that the stream is indeed limited.
     * There is no guarantee either that the stream has at least targetSize bytes.
     * 
     * Implementation is mandatory, an implementation class may not throw NotImplementedException
     * 
     * Implementation classes can choose to actually return a truly limited stream, in case
     * it is more efficient for them to do so (for example, by passing in a Range: HTTP header).
     * 
     * @param targetSize The minimal size that the returned stream should cover *if* the underlying
     * data is large enough.  If targetSize is -1, then this method should be equivalent to stream()
     */
    public InputStream headStream(long targetSize) throws IOException;
}
