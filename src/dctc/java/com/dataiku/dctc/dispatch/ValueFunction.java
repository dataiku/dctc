package com.dataiku.dctc.dispatch;

import com.dataiku.dip.datalayer.Column;
import com.dataiku.dip.datalayer.Row;

public class ValueFunction implements SplitFunction {
    public ValueFunction() {
    }
    public String split(Row row, Column column) {
        assert(column != null);

        String splitData = row.get(column);

        if (splitData == null) {
            return "__no_value__";
        }

        return splitData;
    }
}
