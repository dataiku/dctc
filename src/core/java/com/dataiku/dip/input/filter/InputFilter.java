package com.dataiku.dip.input.filter;

import java.util.List;

import com.dataiku.dip.partitioning.Partition;


public class InputFilter {
    public List<Partition> selectablePartitions;
	/* TODO: Make this a real AST */
	public List<FilterClause> conjunctiveClauses;
}
