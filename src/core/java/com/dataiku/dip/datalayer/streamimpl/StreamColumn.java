package com.dataiku.dip.datalayer.streamimpl;

import com.dataiku.dip.datalayer.Column;

public class StreamColumn extends Column {
	// This is package protected for access by the StreamColumnFactory
    String name;
    StreamColumnFactory factory;
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public StreamColumn setName(String name) {
        factory.renameColumn(this, name);
        return this;
    }

    @Override
    public String toString() {
        return "[SC:" + name + "]";
    }
}