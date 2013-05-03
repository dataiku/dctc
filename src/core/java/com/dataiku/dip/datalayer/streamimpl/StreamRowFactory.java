package com.dataiku.dip.datalayer.streamimpl;

import com.dataiku.dip.datalayer.Row;
import com.dataiku.dip.datalayer.RowFactory;

public class StreamRowFactory implements RowFactory{
    @Override
    public Row row() {
        return new StreamRow();
    }
}
