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
public abstract class SingleInputSingleOutputRowProcessor implements SingleInputRowProcessor, SingleOutputRowProcessor, ProcessorWithFactories, Processor {
    /** Called after output and factories are set */
    public abstract void init() throws Exception;

    // Getter/Setter
    public ProcessorOutput getProcessorOutput() {
        return out;
    }
    @Override
    public void setProcessorOutput(ProcessorOutput out) {
        this.out = out;
    }
    public SingleInputSingleOutputRowProcessor withProcessorOutput(ProcessorOutput out) {
        this.out = out;
        return this;
    }
    public ColumnFactory getCf() {
        return cf;
    }
    public void setCf(ColumnFactory cf) {
        this.cf = cf;
    }
    public SingleInputSingleOutputRowProcessor withCf(ColumnFactory cf) {
        this.cf = cf;
        return this;
    }
    public RowFactory getRf() {
        return rf;
    }
    public void setRf(RowFactory rf) {
        this.rf = rf;
    }
    public SingleInputSingleOutputRowProcessor withRf(RowFactory rf) {
        this.rf = rf;
        return this;
    }
    public Context getContext() {
        return context;
    }
    public void setContext(Context context) {
        this.context = context;
    }
    public SingleInputSingleOutputRowProcessor withContext(Context context) {
        this.context = context;
        return this;
    }

    @Override
    public void setFactories(ColumnFactory cf, RowFactory rf) {
        this.cf = cf;
        this.rf = rf;
    }

    private Context context;
    private RowFactory rf;
    private ColumnFactory cf;
    private ProcessorOutput out;

}
