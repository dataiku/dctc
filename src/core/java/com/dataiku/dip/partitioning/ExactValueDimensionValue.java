package com.dataiku.dip.partitioning;

public class ExactValueDimensionValue extends DimensionValue {
    public ExactValueDimensionValue(String value) {
        this.value = value;
    }
    public String toString() {
        return "<EV " + value + ">";
    }

    private String value;

    @Override
    public DimensionValue clone() {
       return new ExactValueDimensionValue(value);
    }
    @Override
    public String id() {
        return value;
    }
}
