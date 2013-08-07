package com.dataiku.dip.input.formats;

import com.dataiku.dip.datasets.Schema;

import java.util.ArrayList;

import org.apache.log4j.Logger;

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
    public void setSchema(Schema schema) {
        logger.info("Set schema");
        this.schema = schema;
        types = new ArrayList<Schema.Type>();
        for(Schema.SchemaColumn column : schema.getColumns()) {
            types.add(Schema.Type.forName(column.getType()));
        }
    }
    public Schema getSchema() {
        return schema;
    }
    public ArrayList<Schema.Type> getTypes() {
        return types;
    }

    protected Schema schema;
    protected ArrayList<Schema.Type> types;
    private static Logger logger = Logger.getLogger("dku.format.csv");
}