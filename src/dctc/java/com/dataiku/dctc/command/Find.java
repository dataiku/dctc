package com.dataiku.dctc.command;

import java.util.List;

import java.io.IOException;

import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dip.utils.IndentedWriter;

public class Find extends Command {
    public String tagline() {
        return "Recursively list the content of a location.";
    }
    public void longDescription(IndentedWriter printer) {
        printer.print("Output contains one line per file, with its absolute address");
    }
    // Public
    @Override
    public final void perform(String[] args) {
        List<GeneralizedFile> arguments = getArgs(args);
        if (arguments != null) {
            if (arguments.size() == 0) {
                String[] l = { "." };
                perform(build(l));
            } else {
                perform(arguments);
            }
        }
    }
    @Override
    public void perform(List<GeneralizedFile> args) {
        try {
            for (GeneralizedFile arg: args) {
                if (!arg.exists()) {
                    notFind(arg.givenName());
                } else {
                    if (arg.isDirectory()) {
                        for (GeneralizedFile f: arg.grecursiveList()) {
                            accept(f);
                        }
                    } else {
                        accept(arg);
                    }
                }
            }
        } catch (IOException e) {
            error("failed to find", e, 1);
        }
    }
    @Override
    public String cmdname() {
        return "find";
    }
    /// Getters
    public String pattern() {
        if (name == null) {
            name = getOptionValue("name");
        }
        return name;
    }
    /// Setters
    public Find pattern(String name) {
        this.name = name;
        return this;
    }

    // Protected
    @Override
    protected Options setOptions() {
        Options options = new Options();

        OptionBuilder.withArgName("pattern");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("Print the file only if its name contains the specified pattern.");
        options.addOption(OptionBuilder.create("name"));
        return options;
    }
    @Override
    protected String proto() {
        return "dctc find [OPT...] [PATH]";
    }

    // Private
    private void accept(GeneralizedFile path) throws IOException {
        if (!hasName() || path.getFileName().indexOf(pattern()) != -1) {
            System.out.println(path.givenName());
        }
    }
    private void notFind(String file) {
        error(file, "No such file or directory", 2);
    }
    private boolean hasName() {
        return getOptionValue("name") != null;
    }

    // Attributes
    private String name = null;
}
