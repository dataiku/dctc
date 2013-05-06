package com.dataiku.dctc.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;

import com.dataiku.dctc.AutoGZip;
import com.dataiku.dctc.file.GeneralizedFile;

public class Grep extends Command {
    public String tagline() {
        return "Match content within files.";
    }
    public String longDescription() {
        return "Search the pattern in input files and outputs the lines that match.\n" +
                "The pattern is handled as a non-anchored regular expression.\n" + 
                "\n" +
                "dctc grep only offers a tiny subset of the capabilities of POSIX or GNU grep.\n" +
                "It is intended as a fallback for systems that do not offer a native grep \n"+
                "(eg, Microsoft Windows). For UNIX systems, use dctc cat|grep";
    }
    
    protected Options setOptions() {
        Options options = new Options();

        options.addOption("r", "recursive", false, "Read all files under each directory, recursively.");
        options.addOption("v", "invert-match", false, "Invert the sense of matching, to select non-matching lines.");
        options.addOption("i", "ignore-case", false, "Ignore case distinctions in both the PATTERN and the input files.");

        OptionBuilder.withDescription("Add color to the output");
        options.addOption(OptionBuilder.create("color"));

        return options;
    }

    // Public
    @Override
    public void perform(String[] args) {
        List<GeneralizedFile> arguments = getArgs(args);
        if (arguments != null) {
            perform(arguments, pattern);
        }
    }
    @Override
    public void perform(List<GeneralizedFile> args) {
        perform(args, pattern);
    }
    public void perform(List<GeneralizedFile> args, String pattern) {
        if (args.size() < 1) {
            usage();
            exitCode(2);
        }
        pattern = formatPattern(pattern);
        if (ignoreCase()) {
            pattern = pattern.toLowerCase();
        }
        try {
            boolean header = args.size() > 1;
            for (GeneralizedFile arg: args) {
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
        } catch (IOException e) {
            // FIXME TODO XXX: We need an error(string, exception) method that WRITES THE EXCEPTION
            error("grep failed: " + e, 1);
        }
    }
    @Override
    public String cmdname() {
        return "grep";
    }
    /// Getters
    public boolean ignoreCase() {
        if (ignoreCase == null) {
            ignoreCase = hasOption("i");
        }
        return ignoreCase;
    }
    public boolean invert() {
        if (invert == null) {
            invert = hasOption("v");
        }
        return invert;
    }
    public boolean recursive() {
        if (recursive == null) {
            recursive = hasOption("r");
        }
        return recursive;
    }
    public String formatPattern(String pattern) {
        pattern = ".*" + pattern + ".*";
        return pattern;
    }
    public boolean color() {
        if (color == null) {
            color = hasOption("color");
        }
        return color;
    }

    // Protected
    @Override
    protected String proto() {
        return "dctc grep [OPT...] PATTERN FILES...";
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
        pattern = formatPattern(pattern);
        BufferedReader i =  new BufferedReader(new InputStreamReader(AutoGZip.buildInput(file)));
        try {
            while(true) {
                String line = i.readLine();
                if (line == null) {
                    break;
                }
                matchAndPrint(file, line, pattern, header);
            }
        } finally {
            IOUtils.closeQuietly(i);
        }
    }
    
    private void matchAndPrint(GeneralizedFile file, String line, String pattern, boolean header) {
        if (match(line, pattern)) {
            if (header) {
                if (color()) {
                    System.out.print("\u001B[0;35m");
                }
                System.out.print(file.givenName());
                if (color()) {
                    System.out.print("\u001B[0;36m");
                }
                System.out.print(":");
                if (color()) {
                    System.out.print("\u001B[0m");
                }
            }
            if (color()) {
                System.out.println(line.replaceAll(this.pattern,
                        "\u001B[1;31m" + this.pattern +"\u001B[0m"));
            } else {
                System.out.println(line);
            }
        }
    }
    private boolean match(String line, String pattern) {
        String cmd; {
            if (ignoreCase()) {
                cmd = line.toLowerCase();
            }
            else {
                cmd = line;
            }
        }
        return cmd.matches(pattern) ^ invert();
    }

    // Attributes
    private Boolean ignoreCase = null;
    private Boolean invert = null;
    private Boolean recursive = null;
    private Boolean color = null;
    private String pattern;
}
