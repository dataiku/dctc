package com.dataiku.dctc.command.cat;

import com.dataiku.dctc.file.GeneralizedFile;

public interface CatAlgorithm {
    public void run(GeneralizedFile file);
    public int getExitCode();
}
