package com.dataiku.dip.input.filter;

import java.util.ArrayList;
import java.util.List;

import com.dataiku.dip.partitioning.Partition;

/**
 * The basic result of the execution of a filter : a restriction
 * to a set of partitions, and whether this is enough to completely execute
 * the filter/sampler 
 */
public class FilterResult {
    public boolean isNeedsRefilter() {
        return needsRefilter;
    }
    public void setNeedsRefilter(boolean needsRefilter) {
        this.needsRefilter = needsRefilter;
    }
    public List<Partition> getMatchingPartitions() {
        return matchingPartitions;
    }
    
    private boolean needsRefilter;
    private List<Partition> matchingPartitions = new ArrayList<Partition>();
}