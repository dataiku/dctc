package com.dataiku.dip.datalayer;

public class DevNull implements ProcessorOutput {
    @Override
    public void emitRow(Row row) throws Exception {
    }
    @Override
    public void lastRowEmitted() throws Exception {
    }
}
