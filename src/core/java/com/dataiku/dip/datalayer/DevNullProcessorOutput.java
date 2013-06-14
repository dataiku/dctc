package com.dataiku.dip.datalayer;


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
