package com.dataiku.dctc.command;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import com.dataiku.dctc.clo.FJavaLongOption;
import com.dataiku.dctc.clo.OptionAgregator;
import com.dataiku.dctc.clo.WithArgOptionAgregator;
import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dctc.file.GFile;
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
        resetExitCode();
        List<GFile> arguments = getArgs(args);
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
    public void perform(List<GFile> args) {
        try {
            for (GFile arg: args) {
                if (!arg.exists()) {
                    notFind(arg);
                } else {
                    if (arg.isDirectory()) {
                        for (GFile f: arg.grecursiveList()) {
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
    public Pattern pattern() {
        if (pattern == null && hasOption("name")) {
            pattern = Pattern.compile(getOptionValue("name"));
        }
        return pattern;
    }
    /// Setters
    public Find pattern(String name) {
        this.pattern = Pattern.compile(name);
        return this;
    }
    private OptionAgregator stdOpt(String lgOpt, String description, String argName) {
        return new WithArgOptionAgregator()
            .withOpt(new FJavaLongOption().withOpt(lgOpt))
            .withDescription(description)
            .withArgumentName(argName);
    }

    // Protected
    @Override
    protected void setOptions(List<OptionAgregator> opts) {
        opts.add(stdOpt("name", "Print the file only if its name contains the specified pattern.", "PATTERN"));
        opts.add(stdOpt("type", "Select the type of the file to print (d(irectory), f(ile), a(all)).", "c"));
    }
    @Override
    protected String proto() {
        return "[OPT...] [PATH]";
    }

    // Private
    private void accept(GFile path) throws IOException {
        if (!hasName() || pattern.matcher(path.getFileName()).matches()) {
            switch (kind()) {
            case ALL:
                break;
            case FILE:
                if (!path.isFile()) {
                    return;
                }
                break;
            case DIRECTORY:
                if (!path.isDirectory()) {
                    return;
                }
                break;
            default:
                break;
            }
            System.out.println(path.givenName());
        }
    }
    private void notFind(GFile file) {
        error(file, "No such file or directory", 2);
    }
    private boolean hasName() {
        return pattern() != null;
    }

    private enum Kind {
        ALL
        , FILE
        , DIRECTORY
    }
    private Kind kind() {
        if (kind == null) {
            String value = getOptionValue("type");
            if (value == null || "all".startsWith(value)) {
                kind = Kind.ALL;
            }
            else if ("directory".startsWith(value)) {
                kind = Kind.DIRECTORY;
            }
            else if ("file".startsWith(value)) {
                kind = Kind.FILE;
            }
            else {
                kind = Kind.ALL;
            }
        }
        return kind;
    }

    // Attributes
    private Pattern pattern = null;
    private Kind kind = null;
}
