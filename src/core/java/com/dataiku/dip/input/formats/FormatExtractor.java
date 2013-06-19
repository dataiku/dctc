package com.dataiku.dip.input.formats;

import com.dataiku.dip.datalayer.ColumnFactory;
import com.dataiku.dip.datalayer.ProcessorOutput;
import com.dataiku.dip.datalayer.RowFactory;
import com.dataiku.dip.datasets.Schema;
import com.dataiku.dip.input.StreamInputSplitProgressListener;
import com.dataiku.dip.input.stream.StreamsInputSplit;

public interface FormatExtractor {
    public void setSchema(Schema schema);
    /**
     * Returns true if extraction did not hit any limit, false if it did hit a limit
     */
	public boolean run(StreamsInputSplit in, ProcessorOutput out, ProcessorOutput err,
					ColumnFactory cf, RowFactory rf, StreamInputSplitProgressListener listener,
					ExtractionLimit limit) throws Exception;
}
