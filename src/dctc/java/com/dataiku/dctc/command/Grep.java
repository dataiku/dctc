package com.dataiku.dctc.command;

import static com.dataiku.dip.utils.PrettyString.eol;
import static com.dataiku.dip.utils.PrettyString.scat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.dataiku.dctc.AutoGZip;
import com.dataiku.dctc.clo.Option;
import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dctc.command.grep.GrepHeaderPrinter;
import com.dataiku.dctc.command.grep.GrepLinePrinter;
import com.dataiku.dctc.command.grep.GrepMatcher;
import com.dataiku.dctc.command.grep.GrepPrinter;
import com.dataiku.dctc.command.grep.GrepStrategyFactory;
import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dip.utils.DKUFileUtils;
import com.dataiku.dip.utils.IndentedWriter;
import com.dataiku.dip.utils.StreamUtils;

public class Grep extends Command {
    public String tagline() {
        return "Match content within files.";
    }
    public void longDescription(IndentedWriter printer) {
        printer.paragraph(scat("Search the pattern in input files and outputs the lines that match."
                               , "The pattern is handled as a non-anchored regular expression.")
                          ,scat("dctc grep only offers a tiny subset of the capabilities of"
                                , "POSIX or GNU grep. It is intended as a fallback for systems that"
                                , "do not offer a native grep (eg, Microsoft Windows). For UNIX"
                                , "systems, use dctc cat|grep"));
    }
    protected List<Option> setOptions() {
        List<Option> opts = new ArrayList<Option>();

        opts.add(stdOption('r', "recursive", "Read all files under each directory, recursively."));
        opts.add(stdOption('v', "invert-match", "Invert the sense of matching, to select non-matching lines."));
        opts.add(stdOption('i', "ignore-case", "Ignore case distinctions in both the PATTERN and the input files."));
        opts.add(stdOption('c', "count", "Write only a count of selected lines to standard output."));
        opts.add(stdOption('q', "quiet", "Nothing shall be written to the standard output, regardless of matching lines. Exit with zero status if an input line is selected."));
        opts.add(stdOption('n', "line", "Precede each output line by its relative line number in the file, each file starting at line 1. The line number counter shall be reset for each file processed."));
        opts.add(stdOption('G', "color", "Add color to the output"));
        opts.add(stdOption('E', "extended-regexp", "Match using java extended regular expressions."));
        opts.add(stdOption('F', "fixed-strings", "Match using fixed strings."));
        opts.add(stdOption('s', "no-messages", "Suppress the error messages ordinarily written for nonexistent or unreadable files. Other error messages shall not be suppressed."));
        opts.add(stdOption('l', "files-with-matches", "print only names of FILEs containing matches"));
        opts.add(stdOption('x', "Consider only input lines that use all characters in the line excluding the terminating <newline> to match an entire fixed string or regular expression to be matching lines."));

        opts.add(stdOption('f', "file", "obtain PATTERN from FILE", true, "FILE"));
        opts.add(stdOption('e', "regexp", "Specify one or more patterns to be used during the search for input.", true, "PATTERN"));

        return opts;
    }
    protected List<GeneralizedFile> getArgs(String[] shellArgs) {
        parseCommandLine(shellArgs);
        List<String> args = getArgs();
        { // Set pattern
            if (hasOption('e')) {
                pattern = getOptionValue('e');
            }
            else if (hasOption('f')) {
                try {
                    pattern = DKUFileUtils.fileToString(new File(getOptionValue('f')));
                }
                catch (Exception e) {
                    // FIXME: If -s, be quiet
                    error("Unexpected error while reading " + getOptionValue('f'), e, 3);
                }
            }
            else {
                if (!args.isEmpty()) {
                    pattern = args.get(0);
                    args.remove(0);
                }
            }
        }

        return resolveGlobbing(args);
    }

    // Public
    @Override
    public void perform(String[] args) {
        resetExitCode();
        List<GeneralizedFile> arguments = getArgs(args);
        if (arguments != null) {
            buildMe(arguments.size() > 1);
            perform(arguments);
        }
    }
    public boolean printFileError() {
        if (printFileError == null) {
            printFileError = !hasOption('s');
        }
        return printFileError;
    }
    public void perform(List<GeneralizedFile> args) {
        if (args.size() < 1) {
            usage();
            setExitCode(2);
        }

        for (GeneralizedFile arg: args) {
            try {
                if (!arg.exists()) {
                    noSuch(arg);
                    continue;
                }
                if (arg.isDirectory()) {
                    if (!recursive()) {
                        isADirectory(arg);
                        continue;
                    }
                    List<? extends GeneralizedFile> sons = arg.grecursiveList();
                    if (sons == null) {
                        continue;

                    }
                    for (GeneralizedFile son: sons) {
                        try {
                            if (son.exists() && son.isFile()) { // FIXME: Catch this
                                grep(son); // FIXME: Not this
                            }
                        }
                        catch (IOException e) {
                            failRead(arg, e);
                        }
                    }

                }
                else {
                    try {
                        grep(arg);
                    }
                    catch (IOException e) {
                        failRead(arg, e);
                    }
                }
            }
            catch (IOException e) {
                error(arg, "grep failed: " + e, 1);

            }
        }
        //printCount();
    }
    @Override
    public String cmdname() {
        return "grep";
    }

    public void buildMe(boolean header) {
        GrepStrategyFactory fact = new GrepStrategyFactory()
            .withInverse(!hasOption('v'))
            .withFullLine(hasOption('x'))
            .withIgnoreCase(hasOption('i'))
            .withLinum(hasOption('n'))
            .withRatexp(hasOption('E'))
            .withHeader(header)
            .withColor(hasOption('G'))
            .withListing(hasOption('l'))
            .withCount(hasOption('c'))
            ;
        matcher = fact.buildMatcher(pattern.split(eol()));
        this.header = fact.buildHeaderPrinter();
        line = fact.buildLinePrinter();
        printer = fact.buildPrinter(this.header, matcher);

        hasMatch = false;
    }

    // Protected
    @Override
    protected String proto() {
        return "[OPT...] PATTERN FILES...";
    }

    // Private
    private void grep(GeneralizedFile file) throws IOException {
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
            if (!hasMatch) {
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

    private boolean recursive() {
        return false;
    }
    private void noSuch(GeneralizedFile file) {
        if (printFileError()) {
            error(file, "No such file or directory", 2);
        }
        else {
            setExitCode(2);
        }
    }
    private void isADirectory(GeneralizedFile dir) {
        if (printFileError()) {
            error(dir, "Is a directory", 2);
        }
        else {
            setExitCode(2);
        }
    }
    private void failRead(GeneralizedFile file, Throwable e) {
        if (printFileError()) {
            error(file, "Failed to read file", e, 2);
        }
        else {
            setExitCode(2);
        }
    }

    // Attributes
    private GrepPrinter printer;
    private GrepMatcher matcher;
    private GrepLinePrinter line;
    private GrepHeaderPrinter header;
    private boolean hasMatch;
    private String pattern;

    private Boolean printFileError;
}
