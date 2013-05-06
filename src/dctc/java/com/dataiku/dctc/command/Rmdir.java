package com.dataiku.dctc.command;

import java.util.List;
import java.io.IOException;

import org.apache.commons.cli.Options;

import com.dataiku.dctc.file.GeneralizedFile;

public class Rmdir extends Command {
    public String tagline() {
        return "Remove empty folders";
    }
    public String longDescription() {
        return "Remove empty folders. If the target folder is not empty, rmdir will error out.";
    }
    
    
    @Override
    public void perform(List<GeneralizedFile> args) {
        for (GeneralizedFile arg: args) {
            try {
                if (!arg.exists()) {
                    error(arg.givenName(), "No such file or directory", 2);
                    continue;
                }
                else if (!arg.isEmpty()) {
                    error(arg.givenName(), "Directory not empty", 2);
                    continue;
                }
                arg.delete();
                if (hasOption("v")) {
                    System.out.println("rmdir: removing directory, `" + arg.givenName() + "'");
                }
            } catch (IOException e) {
                warn("failed to rmdir " + arg.givenName(), e);
            }
        }
    }
    @Override
    public String cmdname() {
        return "rmdir";
    }

    @Override
    protected Options setOptions() {
        Options options = new Options();
        return options;
    }
    @Override
    protected String proto() {
        return "dctc rmdir [OPT...] [FILES...]";
    }
}
