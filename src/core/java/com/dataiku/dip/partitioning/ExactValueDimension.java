package com.dataiku.dip.partitioning;

import java.util.List;

import com.google.common.collect.Lists;

public class ExactValueDimension extends Dimension {
    public ExactValueDimension(String name) {
        super(name);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ExactValueDimension)) return false;
        ExactValueDimension tother = (ExactValueDimension)other;
        return name.equals(tother.name);
    }

    @Override
    public DimensionValue getValueFromId(String id) {
        return new ExactValueDimensionValue(id);
    }

    @Override
    public List<DimensionValue> getValueFromPattern(String pattern) {
        // TODO sanity check ? Numerical Range support ?
        return Lists.newArrayList((DimensionValue) new ExactValueDimensionValue(pattern));
    }

    @Override
    public int hashCode() {
        throw new Error("hashCode not designed");
    }
}
