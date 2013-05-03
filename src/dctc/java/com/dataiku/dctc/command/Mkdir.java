package com.dataiku.dctc.command;

import java.util.List;

import java.io.IOException;

import org.apache.commons.cli.Options;

import com.dataiku.dctc.file.GeneralizedFile;

public class Mkdir extends Command {
    public String tagline() {
        return "Create directories";
    }
    public String longDescription() {
        return "Create directories. Parent directories must exist";
    }
    
    // Public
    @Override
    public void perform(List<GeneralizedFile> args) {
        if (args.size() == 0) {
            usage();
        }

        for (GeneralizedFile arg: args) {
            try {
                if (arg.exists()) {
                    error("cannot create directory `" + arg.givenName() + ": File exists", 1);
                } else {
                    arg.mkdir();
                    if (verbose()) {
                        System.out.println("mkdir: created directory `" + arg.givenName() + "'");
                    }
                }
            } catch (IOException e) {
                error(arg.givenName(), e, 3);
            }
        }
    }
    @Override
    public String cmdname() {
        return "mkdir";
    }

    // Protected
    @Override
    protected Options setOptions() {
        Options options = new Options();
        options.addOption("v", "verbose", false, "Print a message for each created directory");
        return options;
    }
    @Override
    protected String proto() {
        return "dctc mkdir [OPT...] [FILES...]";
    }

    private boolean verbose() {
        if (verbose == null) {
            verbose = hasOption("v");
        }
        return verbose;
    }

    // Attributes
    private Boolean verbose;
}
