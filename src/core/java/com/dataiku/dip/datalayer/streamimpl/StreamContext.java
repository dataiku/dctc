package com.dataiku.dip.datalayer.streamimpl;

import com.dataiku.dip.Context;
import com.dataiku.dip.input.InputSplit;
import com.dataiku.dip.input.formats.FormatExtractor;
import com.dataiku.dip.output.OutputWriter;

public class StreamContext implements Context{
    public InputSplit split;
    public OutputWriter output;

    @Override
    public InputSplit getMainInputSplit(){
        return split;
    }

    @Override
    public FormatExtractor getFormatExtractor() {
        return null;
    }
}
