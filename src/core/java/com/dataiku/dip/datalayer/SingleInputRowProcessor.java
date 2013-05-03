package com.dataiku.dip.datalayer;

public interface SingleInputRowProcessor {
	public void processRow(Row row) throws Exception;
	public void postProcess() throws Exception;
}
