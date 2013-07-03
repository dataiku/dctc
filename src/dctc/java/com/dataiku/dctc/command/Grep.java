package com.dataiku.dctc.command;

import static com.dataiku.dip.utils.PrettyString.eol;
import static com.dataiku.dip.utils.PrettyString.scat;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
                               , "The pattern is handled as a non-anchored regular expression.")
                          ,scat("dctc grep only offers a tiny subset of the capabilities of"
                                , "POSIX or GNU grep. It is intended as a fallback for systems that"
                                , "do not offer a native grep (eg, Microsoft Windows). For UNIX"
                                , "systems, use dctc cat|grep"));
    }

    protected Options setOptions() {
        Options options = new Options();

        options.addOption("r", "recursive", false, "Read all files under each directory, recursively.");
        options.addOption("v", "invert-match", false, "Invert the sense of matching, to select non-matching lines.");
        options.addOption("i", "ignore-case", false, "Ignore case distinctions in both the PATTERN and the input files.");
        options.addOption("c", "count", false, "Write only a count of selected lines to standard output.");
        options.addOption("q", "quiet", false, "Nothing shall be written to the standard output, regardless of matching lines. Exit with zero status if an input line is selected.");
        options.addOption("n", "line", false, "Precede each output line by its relative line number in the file, each file starting at line 1. The line number counter shall be reset for each file processed.");
        options.addOption("G", "color", false, "Add color to the output");
        options.addOption("E", "extended-regexp", false, "Match using java extended regular expressions.");
        options.addOption("F", "fixed-strings", false, "Match using fixed strings."); // Follow the posix specifications. Don't need it... Really.
        options.addOption("s", "no-messages", false, "Suppress the error messages ordinarily written for nonexistent or unreadable files. Other error messages shall not be suppressed.");
        longOpt(options, "Specify one or more patterns to be used during the search for input.", "PATTERN", "e", "regexp");
        options.addOption("l", "files-with-matches", false, "print only names of FILEs containing matches");

        return options;
    }

    protected List<GeneralizedFile> getArgs(String[] shellArgs) {
        parseCommandLine(shellArgs);
        List<String> args = new ArrayList<String>(Arrays.asList(getRawArgs().getArgs()));
        { // Set pattern
            if (hasOption("e")) {
                pattern = getOptionValue("e");
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
            printFileError = !hasOption("s");
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
                error(arg.givenName(), "grep failed: " + e, 1);

            }
        }
        //printCount();
    }
    @Override
    public String cmdname() {
        return "grep";
    }

    public void buildMe(boolean header) {
        // Don't sort. Dependencies between the building methods.
        buildHeaderPrinter(header);
        buildMatcher(pattern.split(eol()));
        buildPrinter();
        buildLinePrinter();
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

    private void buildHeaderPrinter(boolean header) {
        if(header) {
            this.header = new SimpleGrepHeaderPrinter();
            if (count()) {
                this.header = new QuietGrepHeaderPrinter(this.header);
            }
        }
        else {
            this.header = new QuietGrepHeaderPrinter(null);
        }
    }
    private void buildLinePrinter() {
        if (count() || !linum()) {
            line = new OffGrepLinePrinter();
        }
        else if (linum()) {
            if (color()) {
                line = new ColoredGrepLinePrinter();
            }
            else {
                line = new OnGrepLinePrinter();
            }
        }
    }
    private void buildPrinter() {
        if (count()) {
            printer = new CountGrepPrinter(header);
        }
        else if (listing()) {
            if (color()) {
                printer = new ColorFileGrepPrinter();
            }
            else {
                printer = new FileGrepPrinter();
            }
        }
        else if (color()) {
            printer = new ColorGrepPrinter(matcher);
        }
        else {
            printer = new SimpleGrepPrinter();
        }
    }
    private void buildMatcher(String[] pattern) {
        if (ratexp()) {
            matcher = new RatExpGrepMatcher(pattern);
        }
        else {
            matcher = new StringGrepMatcher(pattern);
        }

        if (ignoreCase()) {
            matcher = new IgnoreCaseGrepMatcher(matcher);
        }
        if (inverse()) {
            matcher = new InvGrepMatcher(matcher);
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

    private boolean color() {
        if (color == null) {
            color = hasOption("G");
        }
        return color;
    }
    private boolean listing() {
        if (listing == null) {
            listing = hasOption("l");
        }
        return listing;
    }
    private boolean count() {
        if (count == null) {
            count = hasOption("c");
        }
        return count;
    }
    private boolean inverse() {
        if (inverse == null) {
            inverse = hasOption("v");
        }
        return inverse;
    }
    private boolean ignoreCase() {
        if (ignoreCase == null) {
            ignoreCase = hasOption("i");
        }
        return ignoreCase;
    }
    private boolean ratexp() {
        if (ratexp == null) {
            ratexp = hasOption("E");
        }
        return ratexp;
    }
    private Boolean linum() {
        if (linum == null) {
            linum = hasOption("n");
        }
        return linum;
    }

    // Attributes
    private GrepPrinter printer;
    private GrepMatcher matcher;
    private GrepLinePrinter line;
    private GrepHeaderPrinter header;
    private boolean hasMatch;
    private String pattern;

    // Options
    private Boolean color;
    private Boolean listing;
    private Boolean count;
    private Boolean inverse;
    private Boolean ignoreCase;
    private Boolean ratexp;
    private Boolean linum;

    private Boolean printFileError;
}
