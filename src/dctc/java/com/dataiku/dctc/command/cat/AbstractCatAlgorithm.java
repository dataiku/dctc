package com.dataiku.dctc.command.cat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.dataiku.dctc.AutoGZip;
import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dctc.utils.ExitCode;

abstract class AbstractCatAlgorithm implements CatAlgorithm {
    public AbstractCatAlgorithm(GeneralizedFile file) {
        this.file = file;
    }
    public long run() {
        return _run(file);
    }

    // Abstract method
    protected abstract long _run(GeneralizedFile file);

    // Helper methods
    protected void yell(String error, Throwable exception, int exitCode) {
        // FIXME: Need to be extended
        System.err.println(error);
        setExitCode(exitCode);
    }
    protected InputStream open() {
        InputStream i;
        try { // Open the file
            i = AutoGZip.buildInput(file);
        }
        catch (FileNotFoundException e) {
            yell("dctc cat: " + file.givenName() + ": No such file or directory", e, 1);
            return null;
        }
        catch (IOException e) {
            yell("Could not open file: " + e.getMessage(), e, 2);
            return null;
        }
        return i;
    }

    // Getters-Setters
    public GeneralizedFile getFile() {
        return file;
    }
    public void setFile(GeneralizedFile file) {
        this.file = file;
    }
    public AbstractCatAlgorithm withFile(GeneralizedFile file) {
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

    // Attributes
    private GeneralizedFile file;
    private ExitCode exit;
}
