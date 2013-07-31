package com.dataiku.dctc.command;

import java.io.IOException;
import java.util.List;

import com.dataiku.dctc.clo.OptionAgregator;
import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dctc.file.GFile;
import com.dataiku.dip.utils.IndentedWriter;

public class Mkdir extends Command {
    public String tagline() {
        return "Create directories.";
    }
    public void longDescription(IndentedWriter printer) {
        printer.print("Create directories. Parent directories must exist");
    }

    // Public
    @Override
    public void perform(List<GFile> args) {
        if (args.size() == 0) {
            usage();
        }

        for (GFile arg: args) {
            try {
                if (arg.exists()) {
                    error(arg, "cannot create directory, file exists", 1);
                } else {
                    arg.mkdir();
                    if (verbose()) {
                        System.out.println("mkdir: created directory `"
                                           + arg.givenName() + "'");
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
    protected void setOptions(List<OptionAgregator> opts) {
        opts.add(stdOption('v'
                           , "verbose"
                           , "Print a message for each created directory."));
    }
    @Override
    protected String proto() {
        return "[OPT...] [FILES...]";
    }

    private boolean verbose() {
        if (verbose == null) {
            verbose = hasOption('v');
        }
        return verbose;
    }

    // Attributes
    private Boolean verbose;
}
