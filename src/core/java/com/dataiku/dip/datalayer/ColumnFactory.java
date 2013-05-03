package com.dataiku.dip.datalayer;

public interface ColumnFactory {
	/** Create a column or retrieve an existing one */
	public Column column(String name);
	
	/** Create a column after an existing one */
	public Column columnAfter(String before, String after);
	
	public void deleteColumn(String name);
	
	public Iterable<Column> columns();
}
