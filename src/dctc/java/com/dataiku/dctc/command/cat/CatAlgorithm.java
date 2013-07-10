package com.dataiku.dctc.command.cat;

import com.dataiku.dctc.utils.ExitCode;

public interface CatAlgorithm {
    public long run(); // Returns the number of line printed
    public ExitCode getExitCode();
    public void setExitCode(ExitCode exitCode);
    public CatAlgorithm withExitCode(ExitCode exitCode);
}
