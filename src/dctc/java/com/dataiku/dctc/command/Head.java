package com.dataiku.dctc.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.lang.StringUtils;

import com.dataiku.dctc.AutoGZip;
import com.dataiku.dctc.exception.UserException;
import com.dataiku.dctc.file.GeneralizedFile;

public class Head extends Command {
    public String tagline() {
        return "Output the beginning of files";
    }
    public String longDescription() {
        return "Output the first N lines of the input files";
    }
    
    @SuppressWarnings("static-access")
    protected Options setOptions() {
        Options options = new Options();
        options.addOption(OptionBuilder.withArgName("number").hasArg().withLongOpt("lines").withDescription("Display the first `number' lines of each file").create("n"));
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
        return "dctc head [OPT...] PATHS...";
    }

    // Private
    private void performStdHead() {
        head(System.in);
    }
    private void head(InputStream in) {
        try {
            int n = numberOfLines();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
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
    private void header(GeneralizedFile arg) {
        System.out.print("==> ");
        System.out.print(arg.givenName());
        System.out.println(" <==");
    }

    // Attributes
    private static final int DEFAULT_LINE_NUMBER = 10;
}
