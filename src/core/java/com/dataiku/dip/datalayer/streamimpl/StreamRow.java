package com.dataiku.dip.datalayer.streamimpl;

import java.util.HashMap;
import java.util.Map;

import com.dataiku.dip.datalayer.Column;
import com.dataiku.dip.datalayer.ColumnFactory;
import com.dataiku.dip.datalayer.Row;
import com.dataiku.dip.partitioning.Partition;
import com.google.gson.Gson;

public class StreamRow implements Row{
    @Override
    public void put(Column key, String value) {
        if (value == null || value.length() == 0) {
            delete(key);
        } else { 
            data.put(key.getName(), value);
        }
    }

    @Override
    public void delete(Column key) {
        data.remove(key.getName());
    }
    public Row with(ColumnFactory cf, String key, String value) {
        put(cf.column(key), value);
        return this;
    }
    
    public void put(Column key, int value) { put(key, Integer.toString(value)); }
	public void put(Column key, long value) { put(key, Long.toString(value)); }
	public void put(Column key, double value) { put(key, Double.toString(value)); }
	public void put(Column key, boolean value) { put(key, Boolean.toString(value)); }

	public StreamRow with(Column key, String value) { put(key, value); return this; }
	public StreamRow with(Column key, int value) { put(key, value); return this; }
	public StreamRow with(Column key, long value) { put(key, value); return this; }
	public StreamRow with(Column key, double value) { put(key, value); return this; }
	public StreamRow with(Column key, boolean value) { put(key, value); return this; }

    @Override
    public double getAsDoubleOrNaN(Column cd) {
        try {
            return Double.parseDouble(get(cd));
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    @Override
    public String get(Column cd) {
        return data.get(cd.getName());
    }
    
    @Override
	public String get(Column cd, String defaultValue) {
		String v = get(cd);
		if (v == null || v.length() == 0) return defaultValue;
		return v;
	}

	@Override
	public boolean empty(Column cd) {
		String v = get(cd);
		return v == null || v.length() == 0;
	}
    
    public String toString() {
        return "ROW: " + new Gson().toJson(data);
    }

    @Override
    public void delete() {
        deleted = true;
    }
    @Override
    public boolean isDeleted() {
    	return deleted;
    }
    

    public Partition getSourcePartition() {
        return sourcePartition;
    }

    public void getSourcePartition(Partition partition) {
        this.sourcePartition = partition;
    }


    private Map<String, String> data = new HashMap<String, String>();
    private Partition sourcePartition;
    private boolean deleted;
}
