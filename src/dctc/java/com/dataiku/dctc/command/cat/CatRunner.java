package com.dataiku.dctc.command.cat;

import java.util.List;

import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dctc.file.StandardFile;

public class CatRunner {

    public int perform(List<GeneralizedFile> args, boolean printHeader, CatAlgorithmFactory fact) {
        if (args.size() == 0) {
            args.add(new StandardFile());
        }
        boolean first = true;
        boolean header = args.size() > 1 && printHeader;

        for (GeneralizedFile arg: args) {
            if (header) {
                if (first) {
                    first = false;
                }
                else {
                    System.out.println();
                }
                header(arg);
            }
            CatAlgorithm runner = fact.build(arg);
            runner.run();
            setExitCode(runner.getExitCode());
        }

        return getExitCode();
    }

    // Getters-Setters
    public int getExitCode() {
        return exitCode;
    }
    public void setExitCode(int exitCode) {
        this.exitCode = Math.max(this.exitCode, exitCode);
    }
    public CatRunner withExitCode(int exitCode) {
        setExitCode(exitCode);
        return this;
    }
    public void resetExitCode() {
        this.exitCode = 0;
    }

    // Privates
    private void header(GeneralizedFile file) {
        System.out.println("==> " + file.givenName() + " <==");
    }

    // Attributes
    private int exitCode;
}
