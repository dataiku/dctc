package com.dataiku.dctc.command.cat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.dataiku.dctc.AutoGZip;
import com.dataiku.dctc.command.policy.YellPolicy;
import com.dataiku.dctc.file.GFile;
import com.dataiku.dctc.utils.ExitCode;

abstract class AbstractCatAlgorithm implements CatAlgorithm {
    public AbstractCatAlgorithm(GFile file, String cmdname) {
        this.cmdname = cmdname;
        this.file = file;
    }
    public long run() {
        return _run(file);
    }

    // Abstract method
    protected abstract long _run(GFile file);
    protected String cmdname() {
        return cmdname;
    }

    // Helper methods
    protected void yell(String error
                        , Throwable exception
                        , int exitCode) {
        assert yell != null
            : "yell != null";
        yell.yell(cmdname(), error, exception);
        setExitCode(exitCode);
    }
    protected InputStream open() {
        InputStream i;
        try { // Open the file
            i = AutoGZip.buildInput(file);
        }
        catch (FileNotFoundException e) {
            yell(file.givenName() + ": No such file or directory", e, 1);
            return null;
        }
        catch (IOException e) {
            yell("Could not open file: " + e.getMessage(), e, 2);
            return null;
        }
        return i;
    }

    // Getters-Setters
    public GFile getFile() {
        return file;
    }
    public void setFile(GFile file) {
        this.file = file;
    }
    public AbstractCatAlgorithm withFile(GFile file) {
        setFile(file);
        return this;
    }
    public ExitCode getExitCode() {
        return exit;
    }
    public void setExitCode(ExitCode exit) {
        this.exit = exit;
    }
    public AbstractCatAlgorithm withExitCode(ExitCode exit) {
        setExitCode(exit);
        return this;
    }
    public void setExitCode(int exitCode) {
        exit.setExitCode(exitCode);
    }

    public YellPolicy getYell() {
        return yell;
    }
    public void setYell(YellPolicy yell) {
        this.yell = yell;
    }
    public AbstractCatAlgorithm withYell(YellPolicy yell) {
        setYell(yell);
        return this;
    }
    // Attributes
    private YellPolicy yell;
    private GFile file;
    private ExitCode exit;
    private String cmdname;
}
