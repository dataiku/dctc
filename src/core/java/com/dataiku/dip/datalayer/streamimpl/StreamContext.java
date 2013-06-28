package com.dataiku.dip.datalayer.streamimpl;

import com.dataiku.dip.Context;
import com.dataiku.dip.input.InputSplit;
import com.dataiku.dip.input.formats.FormatExtractor;
import com.dataiku.dip.output.OutputWriter;

public class StreamContext implements Context{
    @Override
    public InputSplit getMainInputSplit(){
        return split;
    }

    @Override
    public FormatExtractor getFormatExtractor() {
        return null;
    }
    public void setMainInputSplit(InputSplit split) {
        this.split = split;
    }
    public StreamContext withMainInputSplit(InputSplit split) {
        this.split = split;
        return this;
    }
    public OutputWriter getOutput() {
        return output;
    }
    public void setOutput(OutputWriter output) {
        this.output = output;
    }
    public StreamContext withOutput(OutputWriter output) {
        this.output = output;
        return this;
    }

    private InputSplit split;
    private OutputWriter output;
}
