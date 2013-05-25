package com.dataiku.dip.partitioning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartitioningScheme {
    public String filePathPattern;
    public boolean ignoreNonMatchingFile = false;
    private List<String> dimensionNames = new ArrayList<String>();
    private Map<String, Dimension> dimensions = new HashMap<String, Dimension>();
    public Bucketing bucketing;

    public boolean isPartitioned() {
        return dimensions.size() > 0;
    }

    public void addDimension(Dimension dimension) {
        dimensionNames.add(dimension.getName());
        dimensions.put(dimension.getName(), dimension);
    }

    public boolean isEqual(PartitioningScheme other) {
        if (!other.dimensionNames.equals(dimensionNames)) return false;
        for (int i = 0; i < dimensionNames.size(); i++) {
            if (!other.dimensions.get(dimensionNames.get(i)).equals(dimensions.get(dimensionNames.get(i)))) {
                return false;
            }
        }
        return true;
    }

    public List<String> getDimensionNames() {
        return dimensionNames;
    }

    public Map<String, Dimension> getDimensions() {
        return dimensions;
    }
}
