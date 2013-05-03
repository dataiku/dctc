package com.dataiku.dip.datalayer;

/**
 * A processor that processes the rows one by one.
 *  - It may mark a row as deleted
 *  - It may not store or use previous row references 
 *  - It is single threaded.
 *  - If it excepts, the row is sent in error
 *  
 * The column factory must be set before init
 */
public abstract class SingleRowProcessor {
	protected ColumnFactory cf;
	
	public void setColumnFactory(ColumnFactory cf) {
		this.cf = cf;
	}
	
	/** Called after the ColumnFactory has been set */
	public abstract void init() throws Exception;
	
	public abstract void processRow(Row row) throws Exception;

	/**
	 * Called after the last row has been processed.
	 * Note that you may not do anything with the rows here. You are basically only
	 * allowed to declare a column as deleted
	 * @throws Exception 
	 */
	public abstract void postProcess() throws Exception;
}