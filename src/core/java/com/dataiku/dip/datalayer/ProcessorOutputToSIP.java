package com.dataiku.dip.datalayer;

import com.dataiku.dip.utils.SingleCallAsserter;


/**
 * Trivial pipe to connect a ProcessorOutput to a SingleInputProcessor
 */
public class ProcessorOutputToSIP implements ProcessorOutput{
    protected SingleInputRowProcessor p;

    public ProcessorOutputToSIP(SingleInputRowProcessor p) {
        this.p = p;
    }

    public SingleInputRowProcessor getTarget() {
        return p;
    }


    @Override
    public void emitRow(Row row) throws Exception {
        if (row.isDeleted()) return;
        p.processRow(row);
    }

    @Override
    public void lastRowEmitted() throws Exception {
        lre.call("Processor already closed");
        p.postProcess();
    }
    private SingleCallAsserter lre = new SingleCallAsserter();
}