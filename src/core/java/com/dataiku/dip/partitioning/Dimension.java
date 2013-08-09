package com.dataiku.dip.partitioning;

import com.google.common.base.Preconditions;

import java.util.List;

public abstract class Dimension {
    public Dimension(String name) {
        this.name = Preconditions.checkNotNull(name);
    }

    protected String name;
    public String getName() {
        return name;
    }

    public abstract DimensionValue getValueFromId(String id);

    public abstract List<DimensionValue> getValueFromPattern(String pattern);

    @Override
    public abstract boolean equals(Object other);
}
