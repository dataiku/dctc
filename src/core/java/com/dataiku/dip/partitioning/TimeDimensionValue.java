package com.dataiku.dip.partitioning;

import java.util.Calendar;

import com.dataiku.dip.partitioning.TimeDimension.Period;
import com.google.common.base.Preconditions;

public class TimeDimensionValue extends DimensionValue implements Comparable<TimeDimensionValue> {
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
    public void setDimension(TimeDimension dimension) {
        this.dimension = dimension;
    }
    public TimeDimensionValue withDimension(TimeDimension dimension) {
        this.dimension = dimension;
        return this;
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
    public TimeDimensionValue withYear(int year) {
        this.year = year;
        return this;
    }
    public TimeDimensionValue withMonth(int month) {
        this.month = month;
        return this;
    }
    public TimeDimensionValue withDay(int day) {
        this.day = day;
        return this;
    }
    public TimeDimensionValue withHour(int hour) {
        this.hour = hour;
        return this;
    }
    @SuppressWarnings("fallthrough")
    public void setCal(Calendar cal) {
        switch(dimension.mappedPeriod) {
        case HOUR:
            setHour(cal.get(Calendar.HOUR));
        case DAY:
            setDay(cal.get(Calendar.DAY_OF_MONTH));
        case MONTH:
            setMonth(cal.get(Calendar.MONTH) + 1);
        case YEAR:
            setYear(cal.get(Calendar.YEAR));
            break;
        default:
            throw new Error("Never reached");
        }
    }
    public TimeDimensionValue withCal(Calendar cal) {
        setCal(cal);
        return this;
    }

    @Override
    public DimensionValue clone() {
        return new TimeDimensionValue()
            .withDimension(dimension)
            .withYear(year)
            .withMonth(month)
            .withDay(day)
            .withHour(hour);
    }
    @Override
    public String id() {
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
            throw new Error("Must not be reached");
        }
    }
    
    /**
     * Replace %Y, %M, %D, %H in the pattern by the elemnets on this value.
     */
    @SuppressWarnings("fallthrough")
    public String resolvePattern(String pattern) {
        switch (dimension.mappedPeriod) {
        /* fallthrough (and lack of break) is intended ! */
        case HOUR:
            pattern = pattern.replace("%H", String.format("%02d", hour));
        case DAY:
            pattern = pattern.replace("%D", String.format("%02d", day));
        case MONTH:
            pattern = pattern.replace("%M", String.format("%02d", month));
        case YEAR:
            pattern = pattern.replace("%Y", String.format("%04d", year));
        }
        return pattern;
    }

    @SuppressWarnings("fallthrough")
    public Calendar getCalendar() {
        Calendar cal = Calendar.getInstance();
        /* fallthrough (and lack of break) is intended ! */
        switch (dimension.mappedPeriod) {
        case HOUR:
            cal.set(Calendar.HOUR_OF_DAY, hour);
        case DAY:
            cal.set(Calendar.DAY_OF_MONTH, day);
        case MONTH:
            cal.set(Calendar.MONTH, month - 1);
        case YEAR:
            cal.set(Calendar.YEAR, year);
            break;
        default:
            assert false : "Must not be reached.";
        }
        return cal;
    }

    public long getTimestamp() {
        return getCalendar().getTimeInMillis();
    }

    /**
     * @return the next period for this time dimension w.r.t to the mapped period
     */
    public TimeDimensionValue nextPeriod() {
        Calendar cal = getCalendar();
        switch (dimension.mappedPeriod) {
            case HOUR:
                cal.add(Calendar.HOUR_OF_DAY, 1) ;
                break;
            case DAY:
                cal.add(Calendar.DATE, 1);
                break;
            case MONTH:
                cal.add(Calendar.MONTH, 1);
                break;
            case YEAR:
                cal.add(Calendar.YEAR, 1);
                break;
        }
        return new TimeDimensionValue(dimension).withCal(cal);
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        return dimension.equals(((TimeDimensionValue) o).dimension);
    }

    @Override
    public int compareTo(TimeDimensionValue timeDimensionValue) {
        return getCalendar().compareTo(timeDimensionValue.getCalendar());
    }

    // Attributes
    private TimeDimension dimension;
    private int year;
    private int month;
    private int day;
    private int hour;
}
