package com.dataiku.dip.input.stream;
import java.io.IOException;

import com.dataiku.dip.input.InputSplit;

/** 
 * One split of a stream input.
 * Each split is actually made of several streams, which can be concatenated
 */
public abstract class StreamsInputSplit implements InputSplit {
    /** Returns false when there is no more stream to read */
    public abstract EnrichedInputStream nextStream() throws IOException;
}
