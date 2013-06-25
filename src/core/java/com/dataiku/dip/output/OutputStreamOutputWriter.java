package com.dataiku.dip.output;

import java.io.IOException;
import java.io.OutputStream;

import com.dataiku.dip.datalayer.ColumnFactory;
import com.dataiku.dip.datalayer.Row;

public class OutputStreamOutputWriter extends OutputWriter{
    private OutputStream os;
    private OutputFormatter formatter;
    private boolean headerEmitted;
    private ColumnFactory cf;

    public OutputStreamOutputWriter(OutputStream os, OutputFormatter formatter) {
        this.os = os;
        this.formatter = formatter;
    }

    @Override
    public void init(ColumnFactory cf) throws IOException {
        this.cf = cf;
    }

    @Override
    public void emitRow(Row row) throws Exception {
        if (!headerEmitted) {
            formatter.header(cf, os);
            headerEmitted = true;
        }
        formatter.format(row, cf, os);
    }

    @Override
    public void lastRowEmitted() throws Exception {
        formatter.footer(cf, os);
    }
}