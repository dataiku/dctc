package com.dataiku.dctc.file;

import java.io.IOException;
import java.io.InputStream;

import com.dataiku.dip.input.stream.AutoEnrichedInputStream;
import com.dataiku.dip.input.stream.EnrichedInputStream;
import com.dataiku.dip.input.stream.StreamsInputSplit;

public class GeneralizedFileInputSplit extends StreamsInputSplit {

    static class GFileStream extends AutoEnrichedInputStream {
        GFile file;
        public GFileStream(GFile file) throws IOException {
            super(file.getSize(), file.getFileName(), file.getFileName());
        }
        @Override
        protected InputStream getBasicInputStream() throws IOException {
            return file.inputStream();
        }
        @Override
        protected InputStream getBasicHeadInputStream(long size)
                throws IOException {
            return file.getRange(0, size);
        }
    }

    private GFile f;
    public GeneralizedFileInputSplit(GFile f)  throws IOException {
        this.f = f;
    }

    public EnrichedInputStream nextStream() throws IOException {
        if (called) return null;
        called = true;
        return new GFileStream(f);
    }
    @Override
    public String getDesc() {
        return f.getFileName();
    }
    @Override
    public void reset() {
        called = false;
    }

    private boolean called = false;
}
