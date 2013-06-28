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
        public String pattern() {
            switch (this) {
            case YEAR:
                return "%Y";
            case MONTH:
                return "%M";
            case DAY:
                return "%D";
            case HOUR:
                return "%H";
            default:
                throw new Error("Never reached.");
            }
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

    @SuppressWarnings("fallthrough")
    @Override
    public DimensionValue getValueFromId(String id) {
        String[] chunks = id.split("-");
        TimeDimensionValue  timeDim = new TimeDimensionValue(this);

        switch (mappedPeriod) {
        case HOUR:
            timeDim.setHour(Integer.parseInt(chunks[3]));
        case DAY:
            timeDim.setDay(Integer.parseInt(chunks[2]));
        case MONTH:
            timeDim.setMonth(Integer.parseInt(chunks[1]));
        case YEAR:
            timeDim.setYear(Integer.parseInt(chunks[0]));

            break;
        default:
            throw new Error("Never reached.");
        }
        return timeDim;
    }

    public int hashCode() {
        assert false : "hashCode not designed";
        return 51; // Viva le pastis
    }
}
