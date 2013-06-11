package com.dataiku.dip.datalayer;

import com.dataiku.dip.datalayer.ProcessorOutput;
import com.dataiku.dip.datalayer.Row;

public class DevNullProcessorOutput implements ProcessorOutput {
    public DevNullProcessorOutput() {
    }
    @Override
    public void emitRow(Row row) throws Exception {
    }

    @Override
    public void lastRowEmitted() throws Exception {
    }
}
