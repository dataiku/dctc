package com.dataiku.dctc.dispatch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.dataiku.dip.datalayer.Column;
import com.dataiku.dip.datalayer.Row;
import com.dataiku.dip.partitioning.TimeDimension;
import com.dataiku.dip.partitioning.TimeDimensionValue;

public class DateFunction implements SplitFunction {
    public DateFunction(String dateFormat, TimeDimension.Period period) {
        this.format = new SimpleDateFormat(dateFormat);
        // "DateFunction" is a magic value and is not used.
        this.dimension = new TimeDimensionValue(new TimeDimension("DateFunction", period));
    }

    public String split(Row row, Column column) {
        assert (column != null)
            : "(column != null)";

        String splitData = row.get(column);
        if (splitData == null) {
            return "no_value";
        }

        try {
            format.setLenient(true);
            Date date = format.parse(splitData);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            dimension.setCal(cal);

            return dimension.id();

        } catch (ParseException e) {
            return "invalid_value";
        }
    }

    private SimpleDateFormat format;
    private TimeDimensionValue dimension;
}
