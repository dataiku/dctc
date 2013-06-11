package com.dataiku.dctc.dispatch;

import com.dataiku.dip.datalayer.Column;
import com.dataiku.dip.datalayer.Row;

public class RandomFunction implements SplitFunction {
    public RandomFunction(int outputSize) {
        this.outputSize = outputSize;
    }
    public synchronized String split(Row row, Column column) {
        ct = ++ct % outputSize;
        return Integer.toString(ct);
    }

    private int outputSize;
    private int ct = 0;
}
