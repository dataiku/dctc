package com.dataiku.dip.partitioning;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class Partition {
    public Partition(PartitioningScheme scheme) {
        this.scheme = scheme;
    }

    public PartitioningScheme getScheme() {
        return scheme;
    }

    public Partition clone() {
        Partition clone = new Partition(this.scheme);

        for (Map.Entry<String, DimensionValue> dv : dimensionValues.entrySet()) {
            System.out.println("CLONE " + dv.getKey() + " WITH VAL " + dv.getValue());
            clone.dimensionValues.put(dv.getKey(), dv.getValue().clone());
        }

        return clone;
    }

    public String toString(){
        return "<partition:" + id() + ">";
    }


    public String id() {
        if (scheme == null || dimensionValues.size() == 0) {
            assert(dimensionValues.size() == 0);
            return "NP";
        } else {
            StringBuilder sb = new StringBuilder();
            boolean empty = true;
            for (DimensionValue dv : dimensionValues.values()) {
                if (empty) {
                    sb.append("|");
                    empty = false;
                }
                sb.append(dv.id());
            }
            return sb.toString();
        }
    }

    public boolean isComplete() {
        for (String dname : scheme.getDimensionNames()) {
            if (dimensionValues.get(dname) == null) return false;
        }
        return true;
    }

    protected PartitioningScheme scheme;

    public void setDimensionValue(String dimension, DimensionValue val) {
        dimensionValues.put(dimension, val);
    }
    public Map<String, DimensionValue> getDimensionValues() {
        return ImmutableMap.copyOf(dimensionValues);
    }

    // Representation of the accepted value for each of the dimensions of the partitioning scheme.
    // For example, on a 2D partitioning : Time(MONTH), Value(country), the list could contain:
    // [2012-01, fr]
    // This is used to identify this partition
    // "Cleanly" formatted output values are provided by the DimensionValues
    protected Map<String, DimensionValue> dimensionValues = new HashMap<String, DimensionValue>();


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((dimensionValues == null) ? 0 : dimensionValues.hashCode());
        result = prime * result + ((scheme == null) ? 0 : scheme.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Partition other = (Partition) obj;
        if (dimensionValues == null) {
            if (other.dimensionValues != null)
                return false;
        } else if (!dimensionValues.equals(other.dimensionValues))
            return false;
        if (scheme == null) {
            if (other.scheme != null)
                return false;
        } else if (!scheme.equals(other.scheme))
            return false;
        return true;
    }
}
