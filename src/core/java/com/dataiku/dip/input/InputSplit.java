package com.dataiku.dip.input;


/**
 * The input split is a parallelism unit:
 * A single thread processes a split.
 * 
 * Some datasets can have several ways of splitting themselves. For example, a filesystem dataset can produce one
 * global split, one split per folder, one split per file, ...
 */
public interface InputSplit {
	/** Get a descriptive identifier for this split */
	public String getDesc();
}