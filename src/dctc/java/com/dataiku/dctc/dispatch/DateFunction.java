package com.dataiku.dctc.dispatch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.dataiku.dip.datalayer.Column;
import com.dataiku.dip.datalayer.Row;
import com.dataiku.dip.partitioning.TimeDimension;

public class DateFunction implements SplitFunction {
    public DateFunction(String dateFormat, TimeDimension.Period period) {
        this.format = new SimpleDateFormat(dateFormat);
        this.period = period;
    }

    public String split(Row row, Column column) {
        assert(column != null);
        String splitData = row.get(column);
        if (splitData == null) {
            return "no_value";
        }
        
        try {
            format.setLenient(true);
            Date date = format.parse(splitData);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            switch (period) {
            case YEAR : return String.format("%04d", cal.get(Calendar.YEAR));
            case MONTH: return String.format("%04d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
            case DAY:return String.format("%04d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, 
                    cal.get(Calendar.DAY_OF_MONTH));
            case HOUR:return String.format("%04d-%02d-%02d:%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, 
                    cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR));
            default:
                throw new Error("Invalid value");
            }
        } catch (ParseException e) {
            return "invalid_value";
        }
    }

    private SimpleDateFormat format;
    private TimeDimension.Period period;
}
