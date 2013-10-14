package com.dataiku.dip.input.formats;

import com.dataiku.dip.datalayer.ColumnFactory;
import com.dataiku.dip.datalayer.ProcessorOutput;
import com.dataiku.dip.datalayer.RowFactory;
import com.dataiku.dip.input.stream.StreamsInputSplit;

public interface BasicFormatExtractor {
    /**
     * Extract a single input split.
     * This method can be called several times on a single extractor
     * Returns true if extraction did not hit any limit, false if it did hit a limit
     */
	public boolean run(StreamsInputSplit in, ProcessorOutput out, ProcessorOutput err,
					ColumnFactory cf, RowFactory rf,
					ExtractionLimit limit) throws Exception;
}
