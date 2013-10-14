package com.dataiku.dip.input.formats;


public abstract class AbstractBasicFormatExtractor implements BasicFormatExtractor {
    public static final String PARAM_skipRowsBeforeHeader = "skipRowsBeforeHeader";
    public static final String PARAM_parseHeaderRow = "parseHeaderRow";
    public static final String PARAM_skipRowsAfterHeader = "skipRowsAfterHeader";
}