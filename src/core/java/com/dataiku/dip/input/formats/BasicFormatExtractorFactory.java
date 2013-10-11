package com.dataiku.dip.input.formats;

import com.dataiku.dip.input.Format;
import com.dataiku.dip.utils.ErrorContext;

public class BasicFormatExtractorFactory {
    public static FormatExtractor build(Format fmt) {
        if (fmt.getType().equals("csv")) {
            BasicCSVFormatConfig csvConfig = new BasicCSVFormatConfig(fmt);
            return new BasicCSVFormatExtractor(csvConfig);
        } else if (fmt.getType().equals("line")) {
            return new BasicLineFormatExtractor(fmt.getParam("charset"));
        } else {
            ErrorContext.throwIAE("Unknown format type "+ fmt.getType());
        }
        throw new Error("never reached");
    }
}
