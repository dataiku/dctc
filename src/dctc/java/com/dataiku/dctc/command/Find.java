package com.dataiku.dctc.command;

import java.io.IOException;
import java.util.ArrayList;
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
        printer.print("Output contains one line per file, with its"
                      + " absolute address");
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
        for (GFile arg: args) {
            try {
                if (!arg.exists()) {
                    notFind(arg);
                }
                else {
                    if (arg.isDirectory()) {
                        for (GFile f: arg.grecursiveList()) {
                            accept(f);
                        }
                    }
                    else {
                        accept(arg);
                    }
                }
            }
            catch (IOException e) {
                error(arg, "Failed to find", e, 2);
            }
        }
    }
    @Override
    public String cmdname() {
        return "find";
    }
    /// Getters
    public List<Pattern> pattern() {
        if (pattern == null) {
            pattern = new ArrayList<Pattern>();
            if (hasOption("name")) {
                for (String name: getOptionValue("name")) {
                    pattern.add(Pattern.compile(name));
                }
            }
        }

        return pattern;
    }
    /// Setters
    public Find pattern(String name) {
        this.pattern.add(Pattern.compile(name));
        return this;
    }
    private OptionAgregator stdOpt(String lgOpt
                                   , String description
                                   , String argName) {
        return new WithArgOptionAgregator()
            .withOpt(new FJavaLongOption().withOpt(lgOpt))
            .withDescription(description)
            .withArgumentName(argName);
    }

    // Protected
    @Override
    protected void setOptions(List<OptionAgregator> opts) {
        opts.add(stdOpt("name"
                        , "Print the file only if its name contains the"
                        + " specified pattern."
                        , "PATTERN"));
        opts.add(stdOpt("type"
                        , "Select the type of the file to print (d(irectory),"
                        + " f(ile), a(ll))."
                        , "c"));
    }
    @Override
    protected String proto() {
        return "[OPT...] [PATH]";
    }

    // Private
    private void accept(GFile path) throws IOException {
        if (hasName()) {
            for (Pattern pat: pattern) {
                if (!pat.matcher(path.getFileName()).matches()) {
                    return;
                }
            }
        }
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
        case NONE:
            return;
        default:
            break;
        }

        System.out.println(path.givenName());
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
        , NONE
    }
    private Kind kind(String kindName) {
            if (kindName == null || "all".startsWith(kindName)) {
                return Kind.ALL;
            }
            else if ("directory".startsWith(kindName)) {
                return Kind.DIRECTORY;
            }
            else if ("file".startsWith(kindName)) {
                return Kind.FILE;
            }
            else {
                return Kind.ALL;
            }
    }
    private Kind kind() {
        if (kind == null) {
            if (hasOption("type")) {
                for (String val: getOptionValue("type")) {
                    if (kind == null) {
                        kind = kind(val);
                    }
                    else {
                        Kind k = kind(val);
                        switch (kind) {
                        case ALL:
                            kind = k;
                            break;
                        case FILE:
                            if (k == Kind.DIRECTORY) {
                                kind = Kind.NONE;
                            }
                            break;
                        case DIRECTORY:
                            if (k == Kind.FILE) {
                                kind = Kind.NONE;
                            }
                            break;
                        case NONE:
                            break;
                        default:
                            throw new Error("Never reached.");
                        }
                    }
                }
            }
            else {
                kind = Kind.ALL;
            }
        }
        return kind;
    }

    // Attributes
    private List<Pattern> pattern = null;
    private Kind kind = null;
}
