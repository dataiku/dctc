package com.dataiku.dip.output;

import org.apache.commons.lang.NotImplementedException;

import com.dataiku.dip.input.Format;
import com.dataiku.dip.input.formats.CSVFormatConfig;

public class OutputFormatterFactory {
    public static OutputFormatter build(Format fmt) {

        if (fmt.getType().equals("csv")) {
            CSVFormatConfig csvConfig = new CSVFormatConfig(fmt);
            CSVOutputFormatter csv = new CSVOutputFormatter(csvConfig.separator);
            return csv;
        } else {
            throw new NotImplementedException();
        }
    }	
}
