package com.dataiku.dctc.command;

import java.util.List;

import org.apache.commons.cli.Options;

import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dctc.command.cat.AlgorithmType;
import com.dataiku.dctc.command.cat.CatAlgorithmFactory;
import com.dataiku.dctc.command.cat.CatRunner;
import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dip.utils.IndentedWriter;

public class Head extends Command {
    public String tagline() {
        return "Output the beginning of files.";
    }
    public void longDescription(IndentedWriter printer) {
        printer.print("Output the first N lines of the input files");
    }
    protected Options setOptions() {
        Options options = new Options();

        longOpt(options, "Display the first `number' lines of each file", "n", "lines", "K");
        longOpt(options, "Print the first k bytes  of each file.", "c", "bytes", "K");
        options.addOption("q", "quiet", false, "Never print headers giving file names");
        return options;
    }

    @Override
    public void perform(List<GeneralizedFile> args) {
        CatAlgorithmFactory fact = new CatAlgorithmFactory()
            .withAlgo(AlgorithmType.HEAD)
            .withSkipLast(number())
            .withIsLineAlgo(isLine());

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
    public long number() {
        nbLines();
        nbBytes();

        return number;
    }

    public void nbLines() {
        if (isLine()) {
            if (hasOption("n")) {
                number = Long.parseLong(getOptionValue("n"));
            } else {
                number = 10;
            }
        }
    }
    public void nbBytes() {
        if (!isLine()) {
            number = Long.parseLong(getOptionValue("c"));
        }
    }
    @Override
    protected String proto() {
        return "[OPT...] PATHS...";
    }
    private boolean isLine() {
        if (isLine == null) {
            isLine = !hasOption("c");
        }

        return isLine;
    }

    // Attributes
    private Boolean quiet;
    private Boolean isLine;
    private long number;
}
