package com.dataiku.dip.partitioning;

import java.util.Collections;
import java.util.List;

public class ExactValueDimension extends Dimension {
    public ExactValueDimension(String name, String type) {
        super(name);
        this.type = type;
    }

    protected String type;



    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ExactValueDimension)) return false;
        ExactValueDimension tother = (ExactValueDimension)other;
        return name.equals(tother.name) && type.equals(tother.type);
    }

    @Override
    public DimensionValue getValueFromId(String id) {
        return new ExactValueDimensionValue(id);
    }

    @Override
    public List<DimensionValue> getValueFromPattern(String pattern) {
        return Collections.singletonList((DimensionValue) new ExactValueDimensionValue(pattern)); // TODO sanity check ? Numerical Range support ?
    }

    @Override
    public int hashCode() {
        throw new Error("hashCode not designed");
    }
}
