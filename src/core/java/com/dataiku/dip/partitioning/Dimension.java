package com.dataiku.dip.partitioning;

import com.google.common.base.Preconditions;

public abstract class Dimension {
    public Dimension(String name) {
        this.name = Preconditions.checkNotNull(name);
    }

    private String name;
    public String getName() {
        return name;
    }

    public abstract DimensionValue getValueFromId(String id);

    @Override
    public abstract boolean equals(Object other);
}
