package com.dataiku.dip.partitioning;

import com.dataiku.dip.utils.ErrorContext;

public class TimeDimension extends Dimension {
    public TimeDimension(String name, Period mappedPeriod) {
        super(name);
        this.mappedPeriod = mappedPeriod;
    }

    public String formatValue(TimeDimensionValue tdv, String glob) {
        return tdv.formatGlob(glob);
    }

    public enum Period {
        YEAR,
        MONTH,
        DAY,
        HOUR;

        public static Period parse(String in) {
            for (Period p : values()) {
                if (p.toString().toLowerCase().equalsIgnoreCase(in)) return p;
            }
            throw ErrorContext.iae("Invalid time period " + in);
        }
    }


    public boolean hasYear(){
        return true;
    }
    public boolean hasMonth() {
        return mappedPeriod != Period.YEAR;
    }
    public boolean hasDay() {
        return mappedPeriod != Period.YEAR && mappedPeriod != Period.MONTH;
    }
    public boolean hasHour() {
        return mappedPeriod != Period.YEAR && mappedPeriod != Period.MONTH && mappedPeriod != Period.DAY;
    }
    public Period mappedPeriod;

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TimeDimension)) return false;
        TimeDimension tother = (TimeDimension)other;
        return tother.mappedPeriod.equals(mappedPeriod);
    }

    @Override
    public DimensionValue getValueFromId(String id) {
        String[] chunks = id.split("-");
        switch (mappedPeriod) {
        case YEAR: return new TimeDimensionValue(this, Integer.parseInt(id));
        case MONTH: return new TimeDimensionValue(this, Integer.parseInt(chunks[0]), Integer.parseInt(chunks[1]));
        case DAY: return new TimeDimensionValue(this,Integer.parseInt(chunks[0]), Integer.parseInt(chunks[1]), Integer.parseInt(chunks[2]));
        case HOUR: return new TimeDimensionValue(this, Integer.parseInt(chunks[0]), Integer.parseInt(chunks[1]), Integer.parseInt(chunks[2]), Integer.parseInt(chunks[3]));
        default: throw new Error("impossible");
        }
    }
}
