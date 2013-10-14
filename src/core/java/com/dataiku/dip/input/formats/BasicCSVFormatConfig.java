package com.dataiku.dip.input.formats;

import com.dataiku.dip.input.Format;
import com.dataiku.dip.utils.WithParams;

public class BasicCSVFormatConfig {
    public BasicCSVFormatConfig() {
    }

    public BasicCSVFormatConfig(WithParams p) {
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
        } else {
            arraySeparator = '\u0002';
        }

        if (!p.getParam("mapKeySeparator", "").isEmpty()) {
            mapKeySeparator = p.getCharParam("mapKeySeparator");
        } else {
            mapKeySeparator = '\u0003';
        }

        if (!p.getParam("arrayMapFormat", "").isEmpty()) {
            arrayMapFormat = p.getParam("arrayMapFormat");
        } else {
            arrayMapFormat = "delimited";
        }

        if (!(arrayMapFormat.equals("json") || arrayMapFormat.equals("delimited"))) {
            throw new IllegalArgumentException("arrayMapFormat: possible values are json or delimited");
        }

        skipRowsBeforeHeader = p.getIntParam(AbstractBasicFormatExtractor.PARAM_skipRowsBeforeHeader, 0);
        parseHeaderRow = p.getBoolParam(AbstractBasicFormatExtractor.PARAM_parseHeaderRow, true);
        skipRowsAfterHeader = p.getIntParam(AbstractBasicFormatExtractor.PARAM_skipRowsAfterHeader, 0);
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

        if (arrayMapFormat != null) {
            f.addParam("arrayMapFormat", arrayMapFormat);
        }

        f.addParam(AbstractBasicFormatExtractor.PARAM_skipRowsBeforeHeader, skipRowsBeforeHeader);
        f.addParam(AbstractBasicFormatExtractor.PARAM_parseHeaderRow, ""+parseHeaderRow);
        f.addParam(AbstractBasicFormatExtractor.PARAM_skipRowsAfterHeader, skipRowsAfterHeader);
    }

    // Attributes
    int probableNumberOfRecords;
    
    public enum CSVStyle {
        OPENCSV,
        RFC,
        ESCAPE_ONLY_NO_QUOTE
    }

    public String charset = "utf8";
    public char separator;
    public char quoteChar;
    public Character escapeChar;
    public Character arraySeparator;
    public Character mapKeySeparator;
    public String arrayMapFormat = "delimited";
    
    public int skipRowsBeforeHeader;
    public boolean parseHeaderRow;
    public int skipRowsAfterHeader;
}
