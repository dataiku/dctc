package com.dataiku.dip.input.formats;

import com.dataiku.dip.input.Format;
import com.dataiku.dip.utils.WithParams;

public class CSVFormatConfig {
	public CSVFormatConfig() {
	}
	
	public CSVFormatConfig(WithParams p) {
	    charset = p.getParam("charset", "");
		separator = p.getCharParam("separator");
		if (p.getParam("quoteChar", null) != null) {
		    quoteChar = p.getCharParam("quoteChar");
		} else {
		    quoteChar = '"';
		}
		headerLines = p.getIntParam("headerLines", 0);
		colHeadersRecord = p.getIntParam("colsHeaderLine", -1);
	}
	public void toParams(Format f) {
	    f.addParam("charset", "" + charset);
		f.addParam("separator", "" + separator);
		f.addParam("quoteChar", "" + quoteChar);
		f.addParam("headerLines", headerLines);
		f.addParam("colsHeaderLine", colHeadersRecord);
	}
	
	public String charset;
	public char separator;
	public char quoteChar;
    public int headerLines;
	public int colHeadersRecord= -1;

	int probableNumberOfRecords;
}