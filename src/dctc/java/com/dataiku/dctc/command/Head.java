package com.dataiku.dctc.command;

import java.util.ArrayList;
import java.util.List;

import com.dataiku.dctc.clo.Option;
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
    protected List<Option> setOptions() {
        List<Option> opts = new ArrayList<Option>();

        opts.add(stdOption('n', "lines", "Display the first `number' lines of each file", true)); // FIXME: K
        opts.add(stdOption('c', "bytes", "Print the first k bytes  of each file.", true)); // FIXME: K
        opts.add(stdOption('q', "quiet", "Never print headers giving file names"));

        return opts;
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
            quiet = hasOption('q');
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
            if (hasOption('n')) {
                assert getOptionValue('n') != null
                    : "getOptionValue('n') != null";
                number = Long.parseLong(getOptionValue('n'));
            } else {
                number = 10;
            }
        }
    }
    public void nbBytes() {
        if (!isLine()) {
            number = Long.parseLong(getOptionValue('c'));
        }
    }
    @Override
    protected String proto() {
        return "[OPT...] PATHS...";
    }
    private boolean isLine() {
        if (isLine == null) {
            isLine = !hasOption('c');
        }

        return isLine;
    }

    // Attributes
    private Boolean quiet;
    private Boolean isLine;
    private long number;
}
