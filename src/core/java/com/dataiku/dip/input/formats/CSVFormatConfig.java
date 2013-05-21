package com.dataiku.dip.input.formats;

import com.dataiku.dip.input.Format;
import com.dataiku.dip.utils.WithParams;

public class CSVFormatConfig {
    public CSVFormatConfig() {
    }

    public CSVFormatConfig(WithParams p) {
        charset = p.getParam("charset", "utf8");
        separator = p.getCharParam("separator");
        if (p.getParam("quoteChar", "") != "") {
            quoteChar = p.getCharParam("quoteChar");
        } else {
            quoteChar = '"';
        }
        if (p.getParam("escapeChar", "") != "") {
            escapeChar = p.getCharParam("escapeChar");
        }

        skipRowsBeforeHeader = p.getIntParam(AbstractFormatExtractor.PARAM_skipRowsBeforeHeader, 0);
        parseHeaderRow = p.getBoolParam(AbstractFormatExtractor.PARAM_parseHeaderRow, true);
        skipRowsAfterHeader = p.getIntParam(AbstractFormatExtractor.PARAM_skipRowsAfterHeader, 0);
    }
    public void toParams(Format f) {
        f.addParam("charset", "" + charset);
        f.addParam("separator", "" + separator);
        f.addParam("quoteChar", "" + quoteChar);
        if (escapeChar != null) {
            f.addParam("escapeChar", "" + escapeChar);
        }

        f.addParam(AbstractFormatExtractor.PARAM_skipRowsBeforeHeader, skipRowsBeforeHeader);
        f.addParam(AbstractFormatExtractor.PARAM_parseHeaderRow, ""+parseHeaderRow);
        f.addParam(AbstractFormatExtractor.PARAM_skipRowsAfterHeader, skipRowsAfterHeader);
    }

    public String charset = "utf8";
    public char separator;
    public char quoteChar;
    public Character escapeChar;

    public int skipRowsBeforeHeader;
    public boolean parseHeaderRow;
    public int skipRowsAfterHeader;

    int probableNumberOfRecords;
}