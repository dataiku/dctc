package com.dataiku.dip.datalayer;

public interface ProcessorOutput {
    public void emitRow(Row row) throws Exception;
    public void lastRowEmitted() throws Exception;
}