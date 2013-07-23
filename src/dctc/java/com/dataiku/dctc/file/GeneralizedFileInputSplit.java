package com.dataiku.dctc.file;

import java.io.IOException;
import java.io.InputStream;

import com.dataiku.dip.input.stream.BasicEnrichedInputStream;
import com.dataiku.dip.input.stream.EnrichedInputStream;
import com.dataiku.dip.input.stream.StreamsInputSplit;

public class GeneralizedFileInputSplit extends StreamsInputSplit {
    public GeneralizedFileInputSplit(GFile f, InputStream stream) throws IOException {
        this.in = new BasicEnrichedInputStream(stream,
                                               f.getSize());
        this.desc = f.getAbsoluteAddress();
    }
    public GeneralizedFileInputSplit(GFile f) throws IOException {
        this(f, f.inputStream());
    }
    public EnrichedInputStream nextStream() throws IOException {
        if (called) return null;
        called = true;
        return in;
    }
    @Override
    public String getDesc() {
        return desc;
    }
    @Override
    public void reset() {
        called = false;
    }

    private BasicEnrichedInputStream in;
    private boolean called = false;
    String desc;
}
