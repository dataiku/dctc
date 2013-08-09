package com.dataiku.dip.input.filter;

import java.util.ArrayList;
import java.util.List;

import com.dataiku.dip.partitioning.Partition;


public class InputFilter {
    public List<Partition> getPartitionsClause() {
        return partitionsClause;
    }
    public void setPartitionsClause(List<Partition> partitionsClause) {
        this.partitionsClause = partitionsClause;
    }
    public InputFilter withSelectedPartition(Partition p) {
        if (this.partitionsClause == null) this.partitionsClause = new ArrayList<Partition>();
        this.partitionsClause.add(p);
        return this;
    }
    public InputFilter withSelectedPartitions(List<Partition> p) {
        if (this.partitionsClause == null) this.partitionsClause = new ArrayList<Partition>();
        this.partitionsClause.addAll(p);
        return this;
    }

    // Attributes
	/* TODO: Make this a real AST */
    // Not yet implemented :)
    //private List<FilterClause> conjunctiveClauses;
    private List<Partition> partitionsClause;
}
