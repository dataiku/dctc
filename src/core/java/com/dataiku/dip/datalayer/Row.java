package com.dataiku.dip.datalayer;

import com.dataiku.dip.partitioning.Partition;

public interface Row {
    public Partition getSourcePartition();
    
	/** Setters */
	public void put(Column key, String value);
    public void put(Column key, int value);
	public void put(Column key, long value);
	public void put(Column key, double value);
	public void put(Column key, boolean value);
		
	/** Fluent setters */
    
    public Row with(Column key, String value);
    public Row with(Column key, int value);
    public Row with(Column key, long value);
    public Row with(Column key, double value);
    public Row with(Column key, boolean value);
    
    /** Deletes the value of this column in the row */
	public void delete(Column key);
    /** Mark this row as deleted */
    public void delete();
    /** Is this row marked for deletion ? */
    public boolean isDeleted();
    
	/** Fluent setter with column name */
    public Row with(ColumnFactory cf, String key, String value);
    
    /** Gets the column value */
    public String get(Column cd);
    /** Get with a default value if not present */
    public String get(Column cd, String defaultValue);
    /** @return true if the value is null or empty */
    public boolean empty(Column cd);
    
    public double getAsDoubleOrNaN(Column cd);
}