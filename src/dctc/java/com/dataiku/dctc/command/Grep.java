package com.dataiku.dctc.command;

import static com.dataiku.dip.utils.PrettyString.scat;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;

import com.dataiku.dctc.AutoGZip;
import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dip.utils.IndentedWriter;
import com.dataiku.dip.utils.StreamUtils;

public class Grep extends Command {
    public String tagline() {
        return "Match content within files.";
    }
    public void longDescription(IndentedWriter printer) {
        printer.paragraph(scat("Search the pattern in input files and outputs the lines that match."
                               ,"The pattern is handled as a non-anchored regular expression.")
                          ,scat("dctc grep only offers a tiny subset of the capabilities of"
                                ,"POSIX or GNU grep. It is intended as a fallback for systems that"
                                ,"do not offer a native grep (eg, Microsoft Windows). For UNIX"
                                ,"systems, use dctc cat|grep"));
    }

    protected Options setOptions() {
        Options options = new Options();

        options.addOption("r", "recursive", false, "Read all files under each directory, recursively.");
        options.addOption("v", "invert-match", false, "Invert the sense of matching, to select non-matching lines.");
        options.addOption("i", "ignore-case", false, "Ignore case distinctions in both the PATTERN and the input files.");
        options.addOption("c", "count", false, "Write only a count of selected lines to standard output.");
        options.addOption("q", "quiet", false, ". Nothing shall be written to the standard output, regardless of matching lines. Exit with zero status if an input line is selected.");
        options.addOption("n", "line", false, "Precede each output line by its relative line number in the file, each file starting at line 1. The line number counter shall be reset for each file processed.");
        options.addOption("G", "color", false, "Add color to the output");
        options.addOption("E", false, "Match using java extended regular expressions.");

        return options;
    }

    // Public
    @Override
    public void perform(String[] args) {
        resetExitCode();
        List<GeneralizedFile> arguments = getArgs(args);
        if (arguments != null) {
            perform(arguments, pattern);
        }
    }
    @Override
    public void perform(List<GeneralizedFile> args) {
        perform(args, pattern);
    }
    private boolean recursive() {
        return false;
    }
    public void perform(List<GeneralizedFile> args, String pattern) {
        if (args.size() < 1) {
            usage();
            setExitCode(2);
        }
        //pattern = formatPattern(pattern);
        // lowerCase here
        boolean header = args.size() > 1;
        for (GeneralizedFile arg: args) {
            try {
                if (!arg.exists()) {
                    error(arg.givenName(), "No such file or directory", 2);
                    continue;
                }
                if (arg.isDirectory()) {
                    if (!recursive()) {
                        error(arg.givenName(), "Is a directory", 2);
                        continue;
                    }
                    List<? extends GeneralizedFile> sons = arg.grecursiveList();
                    if (sons == null) {
                        continue;

                    }
                    for (GeneralizedFile son: sons) {
                        try {
                            if (son.exists() && son.isFile()) {
                                grep(son, pattern, header);
                            }
                        } catch (IOException e) {
                            error("Failed to read file : " + son.getAbsolutePath(), e, 2);
                        }
                    }

                }
                else {
                    try {
                        grep(arg, pattern, header);
                    } catch (IOException e) {
                        error("Failed to read file : " + arg.getAbsolutePath(), e, 2);
                    }
                }
            }
            catch (IOException e) {
                error(arg.givenName(), "grep failed: " + e, 1);

            }
        }
        //printCount();
    }
    @Override
    public String cmdname() {
        return "grep";
    }
    /// Getters

    // Protected
    @Override
    protected String proto() {
        return "[OPT...] PATTERN FILES...";
    }
    @Override
    protected List<String> getFileArguments(String[] list) {
        if (list.length > 0) {
            this.pattern = list[0];
        }
        List<String> res = new ArrayList<String>();
        for (int i = 1; i < list.length; ++i) {
            res.add(list[i]);
        }
        return res;
    }

    private void grep(GeneralizedFile file, String pattern, boolean header) throws IOException {
        buildMe(pattern, header);
        long lineNumber = 0;
        BufferedReader i = StreamUtils.readStream(AutoGZip.buildInput(file), "UTF-8");
        try {
            while(true) {
                String line = i.readLine();
                if (line == null) {
                    break;
                }
                match(file, ++lineNumber, line);
            }
            printer.end(file);
            if (hasMatch) {
                setExitCode(1);
            }
        }
        finally {
            IOUtils.closeQuietly(i);
        }
    }
    private void match(GeneralizedFile file, long lineNumber, String line) {
        if (matcher.match(line)) {
            hasMatch = true;
            header.print(file);
            this.line.print(lineNumber);
            printer.print(line);
        }
    }

    private void buildHeaderPrinter(boolean header) {
        if(header) {
            this.header = new SimpleGrepHeaderPrinter();
            if (hasOption("c")) {
                this.header = new QuietGrepHeaderPrinter(this.header);
            }
        }
        else {
            this.header = new QuietGrepHeaderPrinter(null);
        }
    }
    private void buildLinePrinter() {
        if (hasOption("c") || !hasOption("n")) {
            line = new OffGrepLinePrinter();
        }
        else if (hasOption("n")) {
            if (hasOption("G")) {
                line = new ColoredGrepLinePrinter();
            }
            else {
                line = new OnGrepLinePrinter();
            }
        }
    }
    private void buildPrinter() {
        if (hasOption("c")) {
            printer = new CountGrepPrinter(header);
        }
        else {
            printer = new SimpleGrepPrinter();
        }
    }
    private void buildMatcher(String pattern) {
        matcher = new StringGrepMatcher(pattern);

        if (hasOption("i")) {
            matcher = new IgnoreCaseGrepMatcher(matcher, pattern);
        }
        if (hasOption("v")) {
            matcher = new InvGrepMatcher(matcher);
        }
    }
    public void buildMe(String pattern, boolean header) {
        // Don't sort. Dependencies between the building methods.
        buildHeaderPrinter(header);
        buildPrinter();
        buildMatcher(pattern);
        buildLinePrinter();
        hasMatch = false;
    }

    private GrepPrinter printer;
    private GrepMatcher matcher;
    private GrepLinePrinter line;
    private GrepHeaderPrinter header;
    private boolean hasMatch;

    // Attributes
    private String pattern;
}
