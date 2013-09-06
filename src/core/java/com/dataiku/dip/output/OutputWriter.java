package com.dataiku.dip.output;

import java.io.IOException;

import com.dataiku.dip.datalayer.ColumnFactory;
import com.dataiku.dip.datalayer.ProcessorOutput;

public abstract class OutputWriter implements ProcessorOutput{
    public abstract void init(ColumnFactory cf) throws Exception;
    /** This should be the real number of written bytes (for example, after GZip) */
    public abstract long writtenBytes() throws IOException;
}
