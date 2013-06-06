package com.dataiku.dip.output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import com.dataiku.dip.datalayer.ColumnFactory;
import com.dataiku.dip.datalayer.Row;

public class SingleFileOutputWriter extends OutputWriter{
    private OutputStream finalOutputStream;
    private FileOutputStream fos;
    private boolean headerEmitted;
    private ColumnFactory cf;
    private String path;
    private OutputFormatter formatter;

    public SingleFileOutputWriter(String path, OutputFormatter formatter) {
        this.path = path;
        this.formatter = formatter;
    }

    @Override
    public void init(ColumnFactory cf) throws IOException {
        this.cf = cf;
        fos = new FileOutputStream(new File(path));
        if (path.contains(".gz")) {
            finalOutputStream = new GZIPOutputStream(fos);
        } else {
            finalOutputStream = fos;
        }
    }

    @Override
    public void emitRow(Row row) throws Exception {
        if (!headerEmitted) {
            formatter.header(cf, finalOutputStream);
            headerEmitted = true;
        }
        formatter.format(row, cf, finalOutputStream);
    }

    @Override
    public void lastRowEmitted() throws Exception {
        formatter.footer(cf, finalOutputStream);
        finalOutputStream.close();
        fos.close();
    }
}