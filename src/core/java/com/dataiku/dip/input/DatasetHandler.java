package com.dataiku.dip.input;

import com.dataiku.dip.input.filter.InputFilter;
import com.dataiku.dip.partitioning.Partition;

import java.io.IOException;
import java.util.List;

public interface DatasetHandler {
    public List<Partition> listPartitions() throws Exception;
    public InputSplit getInputSplit(Partition partition) throws Exception;
    public List<InputSplit> getSplits(InputFilter filter) throws Exception;
}