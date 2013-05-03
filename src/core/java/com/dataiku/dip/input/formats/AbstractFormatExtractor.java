package com.dataiku.dip.input.formats;

import com.dataiku.dip.datasets.Schema;

public abstract class AbstractFormatExtractor implements FormatExtractor {
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
