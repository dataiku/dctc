package com.dataiku.dip.output;

import java.io.IOException;

import com.dataiku.dip.datalayer.ColumnFactory;
import com.dataiku.dip.datalayer.ProcessorOutput;

public abstract class StreamOutput implements ProcessorOutput{
	public abstract void init(ColumnFactory cf) throws IOException;
	
}
