package com.dataiku.dip.datalayer;

import com.dataiku.dip.Context;
import com.dataiku.dip.ProcessorWithFactories;

/**
 * A simple processor that has an input, an output, and that can create rows.
 *
 * The initialization callbacks must be called in this order:
 *   - set output, factories, context (no particular order)
 *   - init
 */
public abstract class SingleInputSingleOutputRowProcessor implements SingleInputRowProcessor, SingleOutputRowProcessor, ProcessorWithFactories {
    protected ProcessorOutput out;
    protected ColumnFactory cf;
    protected RowFactory rf;
    protected Context context;

    @Override
    public void setProcessorOutput(ProcessorOutput out) {
        this.out = out;
    }

    @Override
    public void setFactories(ColumnFactory cf, RowFactory rf) {
        this.cf = cf;
        this.rf = rf;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    /** Called after output and factories are set */
    public abstract void init() throws Exception;
}
