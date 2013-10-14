package com.dataiku.dip;

import com.dataiku.dip.input.InputSplit;

public interface Context {
    public InputSplit getMainInputSplit();
}
