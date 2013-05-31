package com.dataiku.dip.input.formats;

import java.util.ArrayList;
import java.util.List;

import com.dataiku.dip.input.Format;
import com.dataiku.dip.utils.ErrorContext;
import com.dataiku.dip.utils.RegexpFieldsBuilder;

public class BasicFormatExtractorFactory {
    public static FormatExtractor build(Format fmt) {
        if (fmt.getType().equals("csv")) {
            CSVFormatConfig csvConfig = new CSVFormatConfig(fmt);
            CSVFormatExtractor csvExtractor = new CSVFormatExtractor(csvConfig);
            return csvExtractor;

        } else if (fmt.getType().equals("fixed")) {
            int fieldId = 0;
            List<Integer> offsets = new ArrayList<Integer>();
            while (true) {
                ErrorContext.push("field " + fieldId);
                try {
                    String prefix = "record." + fieldId;

                    String data = fmt.getParam(prefix, null);
                    if (data == null) break;

                    offsets.add(Integer.parseInt(data));
                    fieldId++;
                } finally {
                    ErrorContext.pop();
                }
            }
            return new FixedWidthFormatExtractor(offsets,
                       fmt.getParams().getIntParam(AbstractFormatExtractor.PARAM_skipRowsBeforeHeader, 0),
                       fmt.getParams().getBoolParam(AbstractFormatExtractor.PARAM_parseHeaderRow, true),
                       fmt.getParams().getIntParam(AbstractFormatExtractor.PARAM_skipRowsAfterHeader, 0));

        } else if (fmt.getType().equals("regexp_fields")) {
            RegexpFieldsBuilder rfb = RegexpFieldsBuilderFactory.build(fmt);
            SmartRegexpFormatExtractor extractor = new SmartRegexpFormatExtractor(rfb);
            return extractor;
        }
        else if (fmt.getType().equals("line")) {
            return new LineFormatExtractor();
        }
        else {
            ErrorContext.throwIAE("Unknown format type "+ fmt.getType());
        }
        throw new Error("never reached");
    }
}
