package com.dataiku.dip.input.filter;

import java.util.List;

import com.dataiku.dip.partitioning.Partition;


public class InputFilter {
    public List<Partition> getSelectablePartitions() {
        return selectablePartitions;
    }
    public void setSelectablePartitions(List<Partition> selectablePartitions) {
        this.selectablePartitions = selectablePartitions;
    }
    public InputFilter withSelectablePartitions(List<Partition> selectablePartitions) {
        this.selectablePartitions = selectablePartitions;
        return this;
    }
    public List<FilterClause> getConjunctiveClauses() {
        return conjunctiveClauses;
    }
    public void setConjunctiveClauses(List<FilterClause> conjunctiveClauses) {
        this.conjunctiveClauses = conjunctiveClauses;
    }
    public InputFilter withConjunctiveClauses(List<FilterClause> conjunctiveClauses) {
        this.conjunctiveClauses = conjunctiveClauses;
        return this;
    }

    // Attributes
	/* TODO: Make this a real AST */
    private List<FilterClause> conjunctiveClauses;
    private List<Partition> selectablePartitions;
}
