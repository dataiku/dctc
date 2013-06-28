package com.dataiku.dip.input.filter;

public class InputSampler {
    public double getRatio() {
        return ratio;
    }
    public void setRatio(double ratio) {
        this.ratio = ratio;
    }
    public InputSampler withRatio(double ratio) {
        this.ratio = ratio;
        return this;
    }

    // Attributes
    private double ratio;
}
