package com.dataiku.dip.datalayer;

public interface ColumnFactory {
	/** Create a column or retrieve an existing one */
	public Column column(String name);
	
	/** Create a column after an existing one */
	public Column columnAfter(String before, String after);
   
	/** Create a column before an existing one */
    public Column columnBefore(String after, String newCol);
	
	/** Gets (does not create) the column after another one. Returns null if "current" does not exist, or if it's the last column */
	public Column getColumnAfter(String current);
	
	public void deleteColumn(String name);
	
	public Iterable<Column> columns();
}
