package com.dataiku.dip.partitioning;

public abstract class DimensionValue {
    public abstract DimensionValue clone();

    public abstract String id();
}
