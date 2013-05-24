package com.dataiku.dip.partitioning;

import com.dataiku.dip.partitioning.TimeDimension.Period;
import com.google.common.base.Preconditions;

public class TimeDimensionValue extends DimensionValue {
    // Constructors
    public TimeDimensionValue(TimeDimension dimension) {
        this.dimension = dimension;
    }
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
    public int getYear() {
        return year;
    }
    public int getMonth() {
        return month;
    }
    public int getDay() {
        return day;
    }
    public int getHour() {
        return hour;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public void setMonth(int month) {
        this.month = month;
    }
    public void setDay(int day) {
        this.day = day;
    }
    public void setHour(int hour) {
        this.hour = hour;
    }
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
    public String toPrettyString() {
        switch (dimension.mappedPeriod) {
        case HOUR:
            return String.format("%04d-%02d-%02d-%02d", year, month, day, hour);
        case DAY:
            return String.format("%04d-%02d-%02d", year, month, day);
        case MONTH:
            return String.format("%04d-%02d", year, month);
        case YEAR:
            return String.format("%04d", year);
        default:
            assert false : "Must not be reached.";
            return null;
        }
    }
    public String formatGlob(String glob) {
        switch (dimension.mappedPeriod) {
        case HOUR:
            glob = glob.replace("%H", String.format("%02d", hour));
        case DAY:
            glob = glob.replace("%D", String.format("%02d", day));
        case MONTH:
            glob = glob.replace("%M", String.format("%02d", month));
        case YEAR:
            glob = glob.replace("%Y", String.format("%04d", year));
            break;
        default:
            assert false : "Must not be reached.";
            return null;
        }
        return glob;
    }

    // Attributes
    private TimeDimension dimension;
    private int year;
    private int month;
    private int day;
    private int hour;
}
