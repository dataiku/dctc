package com.dataiku.dip.datalayer;

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
//    	System.out.println("  pass row to " + p);
        p.processRow(row);
    }
    
    @Override
    public void lastRowEmitted() throws Exception {
       p.postProcess();
    }
}