package com.dataiku.dip.input.formats;

import com.dataiku.dip.input.Format;
import com.dataiku.dip.utils.WithParams;

public class CSVFormatConfig {
    public CSVFormatConfig() {
    }

    public CSVFormatConfig(WithParams p) {
        charset = p.getParam("charset", "utf8");
        separator = p.getCharParam("separator");
        if (!p.getParam("quoteChar", "").isEmpty()) {
            quoteChar = p.getCharParam("quoteChar");
        } else {
            quoteChar = '"';
        }
        if (!p.getParam("escapeChar", "").isEmpty()) {
            escapeChar = p.getCharParam("escapeChar");
        }
        if (!p.getParam("arrayItemSeparator", "").isEmpty()) {
            arraySeparator = p.getCharParam("arrayItemSeparator");
        }
        if (!p.getParam("mapKeySeparator", "").isEmpty()) {
            mapKeySeparator = p.getCharParam("mapKeySeparator");
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
        if (arraySeparator != null) {
            f.addParam("arrayItemSeparator", "" + arraySeparator);
        }
        if (mapKeySeparator != null) {
            f.addParam("mapKeySeparator", ""+ mapKeySeparator);
        }

        f.addParam(AbstractFormatExtractor.PARAM_skipRowsBeforeHeader, skipRowsBeforeHeader);
        f.addParam(AbstractFormatExtractor.PARAM_parseHeaderRow, ""+parseHeaderRow);
        f.addParam(AbstractFormatExtractor.PARAM_skipRowsAfterHeader, skipRowsAfterHeader);
    }

    public int getProbableNumberOfRecords() {
        return probableNumberOfRecords;
    }
    public void setProbableNumberOfRecords(int problableNumberOfRecords) {
        this.probableNumberOfRecords = problableNumberOfRecords;
    }
    public CSVFormatConfig withProbableNumberOfRecords(int probableNumberOfRecords) {
        this.probableNumberOfRecords = probableNumberOfRecords;
        return this;
    }

    // Attributes
    private int probableNumberOfRecords;

    public String charset = "utf8";
    public char separator;
    public char quoteChar;
    public Character escapeChar;
    public Character arraySeparator;
    public Character mapKeySeparator;

    public int skipRowsBeforeHeader;
    public boolean parseHeaderRow;
    public int skipRowsAfterHeader;
}
