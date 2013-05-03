package com.dataiku.dip.datalayer;

public interface RowInputStream {
	/** Returns null when the stream is consumed */
	public Row next();
}
