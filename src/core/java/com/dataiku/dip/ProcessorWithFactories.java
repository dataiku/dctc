package com.dataiku.dip;

import com.dataiku.dip.datalayer.ColumnFactory;
import com.dataiku.dip.datalayer.RowFactory;

public interface ProcessorWithFactories {
    public void setFactories(ColumnFactory cf, RowFactory rf);
}
