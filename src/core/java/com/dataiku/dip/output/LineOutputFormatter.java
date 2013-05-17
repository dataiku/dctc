package com.dataiku.dip.output;

import java.io.IOException;
import java.io.Writer;

import com.dataiku.dip.datalayer.Column;
import com.dataiku.dip.datalayer.ColumnFactory;
import com.dataiku.dip.datalayer.Row;

public class LineOutputFormatter extends StringOutputFormatter {
    public LineOutputFormatter() {
        super("utf8");
    }
    
    private Column lineColumn;

    @Override
    public void format(Row row, ColumnFactory cf, Writer sb) throws IOException {
        assert lineColumn != null : "header() not called";
        String v = row.get(lineColumn);
        if (v != null) {
            sb.append(v);
        }
        sb.append('\n');
    }

    @Override
    public void header(ColumnFactory cf, Writer sb) throws IOException {
        lineColumn = cf.column("line");
    }

    @Override
    public void footer(ColumnFactory cf, Writer sb) throws IOException {
    }
}