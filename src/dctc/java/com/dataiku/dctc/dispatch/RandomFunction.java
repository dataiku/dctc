package com.dataiku.dctc.dispatch;

import com.dataiku.dip.datalayer.Column;
import com.dataiku.dip.datalayer.Row;

public class RandomFunction implements SplitFunction {
    public RandomFunction(int outputSize) {
        this.outputSize = outputSize;
    }
    public synchronized String split(Row row, Column column) {
        ct = (ct + 1) % outputSize;
        return Integer.toString(ct);
    }

    // Attributes
    private int outputSize;
    private int ct = 0;
}
