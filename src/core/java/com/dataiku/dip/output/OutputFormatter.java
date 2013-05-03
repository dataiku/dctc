package com.dataiku.dip.output;

import java.io.IOException;
import java.io.Writer;

import com.dataiku.dip.datalayer.ColumnFactory;
import com.dataiku.dip.datalayer.Row;

public interface OutputFormatter {
	public void header(ColumnFactory cf, Writer sb) throws IOException;
	public void format(Row row, ColumnFactory cf, Writer sb) throws IOException;
	public void footer(ColumnFactory cf, Writer sb) throws IOException;
}
