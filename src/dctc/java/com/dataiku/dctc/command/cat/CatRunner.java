package com.dataiku.dctc.command.cat;

import java.io.IOException;
import java.util.List;

import com.dataiku.dctc.DCTCLog;
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
                DCTCLog.error(fact.getAlgo().toString().toLowerCase()
                              , arg.givenName()+ ": Unexpected error.", e);
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
                    DCTCLog.error(fact.getAlgo().toString().toLowerCase()
                                  , "Error while reading the file", e);
                    continue;
                }
                runner.setExitCode(exitCode);
                nbLinePrinted += runner.run();
                setExitCode(runner.getExitCode());
            }
            else {
                DCTCLog.error(fact.getAlgo().toString().toLowerCase()
                              , arg.givenName() + ": Is a directory");
            }
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

    // Attributes
    private CatHeaderPrinter header;
    private ExitCode exitCode;
}
