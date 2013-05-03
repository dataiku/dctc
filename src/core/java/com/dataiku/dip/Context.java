package com.dataiku.dip;

import com.dataiku.dip.input.InputSplit;
import com.dataiku.dip.input.formats.FormatExtractor;

public interface Context {
    public InputSplit getMainInputSplit();
    public FormatExtractor getFormatExtractor();
}
