package com.dataiku.dip.input;

import java.util.List;

import com.dataiku.dip.input.filter.FilterResultWithSplits;
import com.dataiku.dip.input.filter.InputFilter;
import com.dataiku.dip.partitioning.Partition;

public interface DatasetHandler {
    public List<Partition> listPartitions() throws Exception;
    
    /**
     * Get a single split to handle a single partition
     */
    public InputSplit getPartitionSplit(Partition partition) throws Exception;
    
    /**
     * Get all splits required to handle a given filter (+ information about whether this 
     * completely takes the filter into account).
     * 
     * Using this method does not give you control about how the splits are created, so for
     * FS-Like datasets, you might prefer using the more specific methods that give you raw
     * lists of files to process.
     */
    public FilterResultWithSplits getFilterSplits(InputFilter filter) throws Exception;


    /**
     * Create the underlying resource (table, bucket, ... ) for a managed dataset
     * @throws Exception
     */
    public boolean createManaged() throws Exception;
}