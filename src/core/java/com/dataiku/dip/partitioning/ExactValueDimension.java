package com.dataiku.dip.partitioning;

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
    public int hashCode() {
        throw new Error("hashCode not designed");
    }
}
