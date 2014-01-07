package com.dataiku.dip.partitioning;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;

import com.google.common.collect.ImmutableMap;

public class Partition implements Cloneable, Serializable{
    private static final long serialVersionUID = 1L;

    public Partition(PartitioningScheme scheme) {
        this.scheme = scheme;
    }

    public static Partition randomSamplePartition(PartitioningScheme scheme) {
        Partition p = new Partition(scheme);

        for (String dimName :scheme.getDimensionNames()) {
            Dimension dim = scheme.getDimension(dimName);
            if (dim instanceof TimeDimension) {
                TimeDimension td = (TimeDimension)dim;
                switch (td.mappedPeriod) {
                case DAY:
                    p.dimensionValues.put(dim.getName(), new TimeDimensionValue(td, 2013, 12, 31));
                    break;
                case HOUR:
                    p.dimensionValues.put(dim.getName(), new TimeDimensionValue(td, 2013, 12, 31, 23));
                    break;
                case MONTH:
                    p.dimensionValues.put(dim.getName(), new TimeDimensionValue(td, 2013, 12));
                    break;
                case YEAR:
                    p.dimensionValues.put(dim.getName(), new TimeDimensionValue(td, 2013));
                    break;
                default:
                    throw new Error("unreachable");
                }

            } else if (dim instanceof ExactValueDimension) {
                p.dimensionValues.put(dim.getName(), new ExactValueDimensionValue("sampleval"));
            } else {
                throw new NotImplementedException();
            }
        }
        return p;
    }

    public PartitioningScheme getScheme() {
        return scheme;
    }

    public Partition clone() {
        Partition clone = new Partition(this.scheme);

        for (Map.Entry<String, DimensionValue> dv : dimensionValues.entrySet()) {
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
            boolean first = true;
            for (String name : scheme.getDimensionNames()) {
                if (first == true) {
                    first = false;
                } else {
                    sb.append("|");
                }
                if (dimensionValues.containsKey(name)) {
                    sb.append(dimensionValues.get(name).id());
                } else {
                    sb.append("*");
                }
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
