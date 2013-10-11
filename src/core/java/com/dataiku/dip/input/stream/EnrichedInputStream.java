package com.dataiku.dip.input.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.compress.archivers.ArchiveInputStream;

import com.dataiku.dip.partitioning.Partition;

public interface EnrichedInputStream {
    public enum Compression {
        NONE,
        GZIP,
        BZIP2
    }
    
    public Compression getCompression();
    public boolean isArchive();
    public ArchiveInputStream archiveContent() throws IOException;
    
    /* Is it possible to get the stream several times ? */
    public boolean repeatable();
    public long size() throws IOException;
    public Map<String, String> metas();
    public String desc();
    public Partition getPartition();
    
    /** Returns the "pristine" stream, which is compress if this is a compressed file. */
    public InputStream rawStream() throws IOException;
    
    /** 
     * Returns the "pristine" stream head.
     * @see decompressedHeadStream
     */
    public InputStream rawHeadStream(long targetSize) throws IOException;
    
    /** Return a stream that is guaranteed to be decompressed */
    public InputStream decompressedStream() throws IOException;
    
    /**
     * Returns a stream that *might* be limited to the first "targetSize" bytes of the stream.
     * Note that there is no guarantee that the stream is indeed limited.
     * There is no guarantee either that the stream has at least targetSize bytes.
     * 
     * The stream is guaranteed to be decompressed
     * 
     * Implementation is mandatory, an implementation class may not throw NotImplementedException
     * 
     * Implementation classes can choose to actually return a truly limited stream, in case
     * it is more efficient for them to do so (for example, by passing in a Range: HTTP header).
     * 
     * @param targetSize The minimal size that the returned stream should cover *if* the underlying
     * data is large enough.  If targetSize is -1, then this method should be equivalent to stream()
     */
    public InputStream decompressedHeadStream(long targetSize) throws IOException;
}
