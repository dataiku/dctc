package com.dataiku.dip.partitioning;

import com.dataiku.dip.partitioning.TimeDimension.Period;
import com.google.common.base.Preconditions;

public class TimeDimensionValue extends DimensionValue {

    public TimeDimensionValue(TimeDimension dimension, int year) {
        Preconditions.checkArgument(dimension.mappedPeriod == Period.YEAR);
        this.dimension = dimension;
        this.year = year;
    }
    public TimeDimensionValue(TimeDimension dimension, int year, int month) {
        Preconditions.checkArgument(dimension.mappedPeriod == Period.MONTH);
        this.dimension = dimension;
        this.year = year;
        this.month = month;
    }
    public TimeDimensionValue(TimeDimension dimension, int year, int month, int day) {
        Preconditions.checkArgument(dimension.mappedPeriod == Period.DAY);
        this.dimension = dimension;
        this.year = year;
        this.month = month;
        this.day = day;
    }
    public TimeDimensionValue(TimeDimension dimension, int year, int month, int day, int hour) {
        Preconditions.checkArgument(dimension.mappedPeriod == Period.HOUR);
        this.dimension = dimension;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
    }

    private TimeDimensionValue() {
    }

    public TimeDimension getDimension() {
        return dimension;
    }

    private TimeDimension dimension;
    public int year;
    public int month;
    public int day;
    public int hour;

    @Override
    public DimensionValue clone() {
        TimeDimensionValue clone = new TimeDimensionValue();
        clone.dimension = dimension;
        clone.year = year;
        clone.month = month;
        clone.day = day;
        clone.hour = hour;
        return clone;
    }

    @Override
    public String id() {
        switch (dimension.mappedPeriod) {
        case YEAR : return "" + year;
        case MONTH : return "" + year + "-" + String.format("%02d", month);
        case DAY : return "" + year + "-" + String.format("%02d", month)+ "-" + String.format("%02d", day);
        case HOUR: return "" + year + "-" + String.format("%02d", month)+ "-" + String.format("%02d", day) + String.format("%02d", hour);
        default: throw new Error("impossible happened");
        }
    }
}
