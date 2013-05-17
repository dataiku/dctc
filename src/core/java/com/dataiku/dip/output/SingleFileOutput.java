package com.dataiku.dip.output;

import java.io.IOException;

public class SingleFileOutput implements Output {
    public SingleFileOutput(String path, OutputFormatter formatter) {
        this.path = path;
        this.formatter = formatter;
    }
    private String path;
    private OutputFormatter formatter;

    @Override
    public OutputWriter getWriter() throws IOException {
        return new SingleFileOutputWriter(path, formatter);
    }
}
