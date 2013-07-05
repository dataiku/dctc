package com.dataiku.dctc.utils;

public class ExitCode {

    // Getters - Setters
    public int getExitCode() {
        return exitCode;
    }
    public void setExitCode(int exitCode) {
        this.exitCode = Math.max(this.exitCode, exitCode);
    }
    public ExitCode withExitCode(int exitCode) {
        setExitCode(exitCode);
        return this;
    }
    public void resetExitCode() {
        this.exitCode = 0;
    }

    // Attributes
    private int exitCode;
}

