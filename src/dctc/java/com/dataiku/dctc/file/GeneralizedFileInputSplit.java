package com.dataiku.dctc.file;

import java.io.IOException;
import java.io.InputStream;

import com.dataiku.dip.input.stream.BasicEnrichedInputStream;
import com.dataiku.dip.input.stream.EnrichedInputStream;
import com.dataiku.dip.input.stream.StreamsInputSplit;

public class GeneralizedFileInputSplit extends StreamsInputSplit {
    public GeneralizedFileInputSplit(GeneralizedFile f, InputStream stream) throws IOException {
        this.in = new BasicEnrichedInputStream(stream,
                                               f.getSize());
        this.desc = f.getAbsoluteAddress();
    }
    public GeneralizedFileInputSplit(GeneralizedFile f) throws IOException {
        this(f, f.inputStream());
    }
    public EnrichedInputStream nextStream() throws IOException {
        EnrichedInputStream res = in;
        in = null;
        return res;
    }
    @Override
    public String getDesc() {
        // The file path is the description.
        return desc;
    }

    private BasicEnrichedInputStream in;
    String desc;
}
