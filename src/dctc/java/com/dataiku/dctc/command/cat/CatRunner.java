package com.dataiku.dctc.command.cat;

import java.util.List;

import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dctc.file.StandardFile;
import com.dataiku.dctc.utils.ExitCode;

public class CatRunner {

    public void perform(List<GeneralizedFile> args, boolean printHeader,
                        CatAlgorithmFactory fact, ExitCode exitCode) {
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
            runner.setExitCode(exitCode);
            runner.run();
            setExitCode(runner.getExitCode());
        }
    }

    // Getters-Setters
    public ExitCode getExitCode() {
        return exitCode;
    }
    public void setExitCode(ExitCode exitCode) {
        this.exitCode = exitCode;
    }
    public CatRunner withExitCode(ExitCode exitCode) {
        setExitCode(exitCode);
        return this;
    }

    public void setExitCode(int exitCode) {
        this.exitCode.setExitCode(exitCode);
    }
    public CatRunner withExitCode(int exitCode) {
        setExitCode(exitCode);
        return this;
    }

    // Privates
    private void header(GeneralizedFile file) {
        System.out.println("==> " + file.givenName() + " <==");
    }

    // Attributes
    private ExitCode exitCode;
}

