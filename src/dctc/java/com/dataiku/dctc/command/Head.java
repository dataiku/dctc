package com.dataiku.dctc.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.lang.StringUtils;

import com.dataiku.dctc.AutoGZip;
import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dctc.exception.UserException;
import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dip.utils.IndentedWriter;
import com.dataiku.dip.utils.StreamUtils;

public class Head extends Command {
    public String tagline() {
        return "Output the beginning of files.";
    }
    public void longDescription(IndentedWriter printer) {
        printer.print("Output the first N lines of the input files");
    }
    protected Options setOptions() {
        Options options = new Options();
        OptionBuilder.withArgName("number");
        OptionBuilder.hasArg();
        OptionBuilder.withLongOpt("lines");
        OptionBuilder.withDescription("Display the first `number' lines of each file");
        options.addOption(OptionBuilder.create("n"));
        return options;
    }

    @Override
    public void perform(List<GeneralizedFile> args) {
        // Sanity check
        numberOfLines();
        if (args.size() == 0) {
            performStdHead();
            return;
        }
        boolean header = args.size() > 1;
        for (GeneralizedFile arg: args) {
            if (header) {
                header(arg);
            }
            try {
                head(AutoGZip.buildInput(arg));
            } catch (IOException e) {
                error(e.getMessage(), 3);
            }
        }
    }
    @Override
    public String cmdname() {
        return "head";
    }

    public int numberOfLines() {
        if (hasOption("n")) {
            String val = getOptionValue("n");
            if (!StringUtils.isNumeric(val)) {
                throw new UserException("Invalid value for -n: " + val + ", expected an integer");
            }
            int i = Integer.parseInt(val);
            if (i < 0) {
                throw new UserException("Invalid value for -n: " + val + ", must be positive");
            }
            return i;
        } else {
            return DEFAULT_LINE_NUMBER;
        }
    }

    @Override
    protected String proto() {
        return "[OPT...] PATHS...";
    }

    // Private
    private void performStdHead() {
        head(System.in);
    }
    private void head(InputStream in) {
        try {
            int n = numberOfLines();
            BufferedReader br = StreamUtils.readStream(in);
            try {
                while(true) {
                    if (n-- == 0) {
                        break;
                    }
                    String line = br.readLine();
                    if (line == null) {
                        break;
                    }
                    System.out.println(line);
                }
            } finally {
                br.close();
            }
        } catch (IOException e) {
            error(e.getMessage(), 1);
        }
    }
    private void header(GeneralizedFile arg, boolean first) {
        if (!first) {
            System.out.println();
        }
        System.out.print("==> ");
        System.out.print(arg.givenName());
        System.out.println(" <==");
    }

    // Attributes
    private static final int DEFAULT_LINE_NUMBER = 10;
}
