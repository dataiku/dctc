package com.dataiku.dip.partitioning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;

public class PartitioningScheme implements Serializable {

    public boolean isPartitioned() {
        return dimensions.size() > 0;
    }

    public PartitioningScheme addDimension(Dimension dimension) {
        dimensionNames.add(dimension.getName());
        dimensions.put(dimension.getName(), dimension);
        return this;
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

    /** NEVER ITERATE ON THE MAP ! */
    Map<String, Dimension> getDimensions() {
        return dimensions;
    }
    
    public Set<String> getDimensionNamesSet() {
        return dimensions.keySet();
    }
    
    public Dimension getDimension(String name) {
        return dimensions.get(name);
    }
    
    public boolean isSingleDimension() {
        return dimensionNames.size() == 1;
    }

    public Dimension getSingleDimension() {
        Preconditions.checkState(dimensionNames.size() == 1);
        return dimensions.get(dimensionNames.get(0));
    }
    
    public TimeDimension getSingleTimeDimension() {
        for (Dimension dim : dimensions.values()) {
            if (dim instanceof TimeDimension) return (TimeDimension) dim;
        }
      return null;
    }

    public static String normalizePathForPattern(String path) {
        if (path == null) {
            return null;
        } else if (path.startsWith("/")) {
            return path.substring(1);
        } else {
            return path;
        }
    }

    public String getFilePathPattern() {
        return normalizePathForPattern(filePathPattern);
    }
    public void setFilePathPattern(String filePathPattern) {
        this.filePathPattern = filePathPattern;
    }
    public PartitioningScheme withFilePathPattern(String filePathPattern) {
        this.filePathPattern = filePathPattern;
        return this;
    }
    public boolean getIgnoreNonMatchingFile() {
        return ignoreNonMatchingFile;
    }
    public void setIgnoreNonMatchingFile(boolean ignoreNonMatchingFile) {
        this.ignoreNonMatchingFile = ignoreNonMatchingFile;
    }
    public PartitioningScheme withIgnoreNonMatchingFile(boolean ignoreNonMatchingFile) {
        this.ignoreNonMatchingFile = ignoreNonMatchingFile;
        return this;
    }
    public Bucketing getBucketing() {
        return bucketing;
    }
    public void setBucketing(Bucketing bucketing) {
        this.bucketing = bucketing;
    }
    public PartitioningScheme withBucketing(Bucketing bucketing) {
        this.bucketing = bucketing;
        return this;
    }

    // Attributes
    private Bucketing bucketing;
    private boolean ignoreNonMatchingFile = false;
    private String filePathPattern;
    private List<String> dimensionNames = new ArrayList<String>();
    private Map<String, Dimension> dimensions = new HashMap<String, Dimension>();


}
