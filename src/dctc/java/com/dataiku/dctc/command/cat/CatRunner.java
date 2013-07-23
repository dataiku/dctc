package com.dataiku.dctc.command.cat;

import java.io.IOException;
import java.util.List;

import com.dataiku.dctc.command.policy.YellPolicy;
import com.dataiku.dctc.file.GFile;
import com.dataiku.dctc.file.StandardFile;
import com.dataiku.dctc.utils.ExitCode;

public class CatRunner {
    public void perform(List<GFile> args
                        , CatAlgorithmFactory fact
                        , ExitCode exitCode
                        , boolean resetLineNumbering) {
        if (args.size() == 0) {
            args.add(new StandardFile());
        }
        long nbLinePrinted = 0;

        for (GFile arg: args) {
            header.print(arg);
            boolean isDirectory;
            try {
                isDirectory = arg.isDirectory();
            } catch (IOException e) {
                yell(fact, arg, "Unexpected error.", e);
                continue;
            }
            if (!isDirectory) {
                CatAlgorithm runner;
                try {
                    if (!resetLineNumbering) {
                        fact.setStartingLine(nbLinePrinted + 1);
                    }
                    runner = fact.build(arg);
                }
                catch (IOException e) {
                    yell(fact, arg, "Error while reading the file.", e);
                    continue;
                }
                runner.setExitCode(exitCode);
                nbLinePrinted += runner.run();
                setExitCode(runner.getExitCode());
            }
            else {
                yell(fact, arg, "Is a directory", null);
            }
        }
    }

    private void yell(CatAlgorithmFactory fact
                      , GFile file
                      , String msg
                      , Throwable e) {
        yell.yell(fact.getAlgo().toString().toLowerCase()
                  , file.givenName() + ": " + msg
                  , e);
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
    public CatHeaderPrinter getHeader() {
        return header;
    }
    public void setHeader(CatHeaderPrinter header) {
        this.header = header;
    }
    public CatRunner withHeader(CatHeaderPrinter header) {
        setHeader(header);
        return this;
    }

    public YellPolicy getYell() {
        return yell;
    }
    public void setYell(YellPolicy yell) {
        this.yell = yell;
    }
    public CatRunner withYell(YellPolicy yell) {
        setYell(yell);
        return this;
    }

    // Attributes
    private YellPolicy yell;
    private CatHeaderPrinter header;
    private ExitCode exitCode;
}
