package com.dataiku.dip.input.formats;

import com.dataiku.dip.datasets.Schema;

public abstract class AbstractFormatExtractor implements FormatExtractor {
    public static final String PARAM_skipRowsBeforeHeader = "skipRowsBeforeHeader";
    public static final String PARAM_parseHeaderRow = "parseHeaderRow";
    public static final String PARAM_skipRowsAfterHeader = "skipRowsAfterHeader";
    
    public AbstractFormatExtractor(Schema schema) {
        this.schema = schema;
    }
    public AbstractFormatExtractor() {
    }

    @Override
    public void forceSchema(Schema schema) {
        this.schema = schema;
    }
    public Schema schema() {
        return schema;
    }

    private Schema schema;
}