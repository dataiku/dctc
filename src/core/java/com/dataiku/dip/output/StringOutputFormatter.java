package com.dataiku.dip.output;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.dataiku.dip.datalayer.ColumnFactory;
import com.dataiku.dip.datalayer.Row;

public abstract class StringOutputFormatter implements OutputFormatter {
    public StringOutputFormatter(String charset) {
        this.charset = charset;
    }
    
    private String charset;
    private BufferedWriter bwr;
    
	public void header(ColumnFactory cf, OutputStream os) throws IOException {
	    bwr = new BufferedWriter(new OutputStreamWriter(os, charset));
	    header(cf, bwr);
	}
	public abstract void header(ColumnFactory cf, Writer wr) throws IOException;
	
	public void format(Row row, ColumnFactory cf, OutputStream os) throws IOException {
	    format(row, cf, bwr);
	}
	public abstract void format(Row row, ColumnFactory cf, Writer wr) throws IOException;

	public void footer(ColumnFactory cf, OutputStream os) throws IOException {
	    footer(cf, bwr);
	    bwr.close();
	}
	public abstract void footer(ColumnFactory cf, Writer wr) throws IOException;
}
