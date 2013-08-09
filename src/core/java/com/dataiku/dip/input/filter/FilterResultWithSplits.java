package com.dataiku.dip.input.filter;

import java.util.ArrayList;
import java.util.List;

import com.dataiku.dip.input.InputSplit;
import com.dataiku.dip.partitioning.Partition;

/**
 * The result of the execution of a filter, together with the list of splits
 * to serve it.
 * @see DatasetHandler
 */
public class FilterResultWithSplits extends FilterResult {
    private List<InputSplit> splits = new ArrayList<InputSplit>();

    public List<InputSplit> getSplits() {
        return splits;
    }
    public FilterResultWithSplits withSplit(InputSplit split) {
        splits.add(split);
        return this;
    }
    public FilterResultWithSplits withSplist(List<InputSplit> splits) {
        this.splits.addAll(splits);
        return this;
    }

    public FilterResultWithSplits withMatchingPartition(Partition p) {
        getMatchingPartitions().add(p);
        return this;
    }
    public FilterResultWithSplits withMatchingPartitions(List<Partition> p) {
        getMatchingPartitions().addAll(p);
        return this;
    }

}