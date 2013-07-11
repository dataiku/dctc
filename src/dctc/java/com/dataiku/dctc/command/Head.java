package com.dataiku.dctc.command;

import java.util.List;

import com.dataiku.dctc.clo.Option;
import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dctc.command.cat.AlgorithmType;
import com.dataiku.dctc.command.cat.AlwaysCatHeaderSelector;
import com.dataiku.dctc.command.cat.CatAlgorithmFactory;
import com.dataiku.dctc.command.cat.CatRunner;
import com.dataiku.dctc.command.cat.NeverCatHeaderSelector;
import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dip.utils.IndentedWriter;

public class Head extends Command {
    public String tagline() {
        return "Output the beginning of files.";
    }
    public void longDescription(IndentedWriter printer) {
        printer.print("Output the first N lines of the input files");
    }
    protected void setOptions(List<Option> opts) {
        opts.add(stdOption('n', "lines", "Display the first `number' lines of each file", true, "K"));
        opts.add(stdOption('c', "bytes", "Print the first k bytes  of each file.", true, "K"));
        Option quiet = stdOption('q', "quiet", "Never output headers giving file names.");
        quiet.getLongOption().addOpt("silent");
        opts.add(quiet);
    }

    @Override
    public void perform(List<GeneralizedFile> args) {
        CatAlgorithmFactory fact = new CatAlgorithmFactory()
            .withAlgo(AlgorithmType.HEAD)
            .withSkipLast(number())
            .withIsLineAlgo(isLine())
            .withYell(getYell());

        CatRunner runner = new CatRunner();
        if (args.size() > 1 && !hasOption("quiet")) {
            runner.setHeader(new AlwaysCatHeaderSelector());
        }
        else {
            runner.setHeader(new NeverCatHeaderSelector());
        }

        runner.perform(args, fact, getExitCode(), true);
    }
    @Override
    public String cmdname() {
        return "head";
    }
    public long number() {
        nbLines();
        nbBytes();

        return number;
    }

    public void nbLines() {
        if (isLine()) {
            number = getIntOption('n', 10);
        }
    }
    public void nbBytes() {
        if (!isLine()) {
            number = getIntOption('c', 10);
        }
    }
    @Override
    protected String proto() {
        return "[OPT...] PATHS...";
    }
    private boolean isLine() {
        String line = getLastPosition("lines", "bytes");
        if (line != null) {
            isLine = line.equals("lines");
        }

        return isLine;
    }

    // Attributes
    private Boolean isLine;
    private long number;
}
