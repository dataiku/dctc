package com.dataiku.dip.input.formats;

import com.dataiku.dip.input.Format;
import com.dataiku.dip.utils.ErrorContext;
import com.dataiku.dip.utils.RegexpFieldsBuilder;

public class RegexpFieldsBuilderFactory {
    public static void addMatchStep(Format fmt, int step, String pattern) {
        fmt.addParam("field." + step + ".type", "match");
        fmt.addParam("field." + step + ".pattern", pattern);
    }
    public static void addCaptureStep(Format fmt, int step, String column, String pattern) {
        fmt.addParam("field." + step + ".type", "capture");
        fmt.addParam("field." + step + ".pattern", pattern);
        fmt.addParam("field." + step + ".column",  column);
    }
    public static void addCustomCaptureStep(Format fmt, int step, String column, String pattern) {
        fmt.addParam("field." + step + ".type", "customcapture");
        fmt.addParam("field." + step + ".pattern", pattern);
        fmt.addParam("field." + step + ".column",  column);
    }
    public static void addPredefinedCaptureStep(Format fmt, int step, String column, String pattern) {
        fmt.addParam("field." + step + ".type", "predefcapture");
        fmt.addParam("field." + step + ".predef", pattern);
        fmt.addParam("field." + step + ".column",  column);
    }

    public static RegexpFieldsBuilder build(Format fmt) {
        RegexpFieldsBuilder out = new RegexpFieldsBuilder(fmt.getBoolParam("autoAddSpace", false));

        int fieldId = 0;
        while (true) {
            ErrorContext.push("field " + fieldId);
            try {
                String prefix = "field." + fieldId;

                String fieldType = fmt.getParams().get(prefix, "type");
                if (fieldType == null) break;

                String fieldPattern = fmt.getParams().get("field." + fieldId, "pattern");

                if (fieldType.equals("predefcapture")) {
                    String fieldPredef = fmt.getParams().get("field." + fieldId, "predef");
                    if (fieldPredef == null) {
                        ErrorContext.throwIAE("Neither pattern nor predef specified");
                    }
                    if (fieldPredef.equals("quotted_no_escape")) {
                        fieldPattern = RegexpFieldsBuilder.QUOTTED_NO_ESCAPE;
                        fieldType = "customcapture";
                    } else if (fieldPredef.equals("integer")) {
                        fieldPattern = RegexpFieldsBuilder.INTEGER;
                        fieldType = "capture";
                    } else if (fieldPredef.equals("ip")) {
                        fieldPattern = RegexpFieldsBuilder.IP;
                        fieldType = "capture";
                    } else {
                        ErrorContext.throwIAE("Unknown predef value "  +fieldPredef);
                    }
                }

                if (fieldPattern == null) {
                    ErrorContext.throwIAE("No field pattern nor predef");
                }

                if (fieldType.equals("capture")) {
                    out.capture(fmt.getParams().get(prefix, "column"), fieldPattern);
                } else if (fieldType.equals("match")) {
                    out.match(fieldPattern);
                } else if (fieldType.equals("customcapture")) {
                    out.customCapture(fmt.getParams().get(prefix, "column"), fieldPattern);
                } else {
                    ErrorContext.throwIAE("Unknown field type " + fieldType);
                } 
            } finally {
                ErrorContext.pop();
            }
            fieldId++;
        }
        out.compile();
        return out;
    }
}
