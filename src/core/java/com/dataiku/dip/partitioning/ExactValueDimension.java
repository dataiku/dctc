package com.dataiku.dip.partitioning;

public class ExactValueDimension extends Dimension {
    public ExactValueDimension(String name, String field) {
        super(name);
        this.field = field;
    }

    public String getField() {
        return field;
    }

    private String field;

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ExactValueDimension)) return false;
        ExactValueDimension tother = (ExactValueDimension)other;
        return tother.field.equals(field);
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
