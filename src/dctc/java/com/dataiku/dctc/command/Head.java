package com.dataiku.dctc.command;

import java.util.List;

import org.apache.commons.cli.Options;

import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dctc.command.cat.AlgorithmType;
import com.dataiku.dctc.command.cat.CatAlgorithmFactory;
import com.dataiku.dctc.command.cat.CatRunner;
import com.dataiku.dctc.exception.UserException;
import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dip.utils.IndentedWriter;
import com.dataiku.dip.utils.IntegerUtils;

public class Head extends Command {
    public String tagline() {
        return "Output the beginning of files.";
    }
    public void longDescription(IndentedWriter printer) {
        printer.print("Output the first N lines of the input files");
    }
    protected Options setOptions() {
        Options options = new Options();

        longOpt(options, "Display the first `number' lines of each file", "n", "lines", "number");
        options.addOption("q", "quiet", false, "Never print headers giving file names");

        return options;
    }

    @Override
    public void perform(List<GeneralizedFile> args) {
        CatAlgorithmFactory fact = new CatAlgorithmFactory()
            .withAlgo(AlgorithmType.HEAD)
            .withSkipLast(numberOfLines());

        CatRunner runner = new CatRunner();
        runner.perform(args, !getQuiet(), fact, getExitCode());
    }
    @Override
    public String cmdname() {
        return "head";
    }
    public boolean getQuiet() {
        if (quiet == null) {
            quiet = hasOption("q");
        }
        return quiet;
    }
    public int numberOfLines() {
        if (hasOption("n")) {
            String val = getOptionValue("n");
            if (!IntegerUtils.isNumeric(val)) {
                throw new UserException("Invalid value for -n: " + val + ", expected an integer");
            }
            return IntegerUtils.toInt(val);
        }
        else {
            return DEFAULT_LINE_NUMBER;
        }
    }
    @Override
    protected String proto() {
        return "[OPT...] PATHS...";
    }

    // Attributes
    private Boolean quiet;
    private static final int DEFAULT_LINE_NUMBER = 10;
}
