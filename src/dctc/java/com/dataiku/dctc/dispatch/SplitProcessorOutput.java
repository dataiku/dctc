package com.dataiku.dctc.dispatch;

import com.dataiku.dip.datalayer.ProcessorOutput;
import com.dataiku.dip.datalayer.Row;
import com.dataiku.dip.datalayer.streamimpl.StreamColumnFactory;

public class SplitProcessorOutput implements ProcessorOutput {
    SplitProcessorOutput(SplitStreamFactory fact) {
        this.factory = fact;
    }
    @Override
    public void emitRow(Row row) throws Exception {
        factory.emitRow(columnFactory, row);
    }

    @Override
    public void lastRowEmitted() throws Exception {
    }
    public StreamColumnFactory getColumnFactory() {
        return columnFactory;
    }

    // Attributes
    private StreamColumnFactory columnFactory = new StreamColumnFactory();
    private SplitStreamFactory factory;
}
