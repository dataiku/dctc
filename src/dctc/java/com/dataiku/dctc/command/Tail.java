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

public class Tail extends Command {
    public String tagline() {
        return "Output the end of files.";
    }
    public void longDescription(IndentedWriter printer) {
        printer.print("Output the last N lines or bytes of the input files");
    }
    @Override
    public String cmdname() {
        return "tail";
    }
    @Override
    protected String proto() {
        return "[OPTIONS...] [FILE...]";
    }
    @Override
    protected void setOptions(List<Option> opts) {
        opts.add(stdOption('c', "bytes", "Output the last K bytes.", true, "K"));
        opts.add(stdOption('n', "lines", "Output the last K lines.", true, "K"));
        Option quiet = stdOption('q', "quiet", "Never output headers giving file names.");
        quiet.getLongOption().addOpt("silent");
        opts.add(quiet);
    }
    @Override
    public void perform(List<GeneralizedFile> args) {
        CatAlgorithmFactory fact = new CatAlgorithmFactory()
            .withAlgo(AlgorithmType.TAIL)
            .withSkipFirst(number())
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
    public long number() {
        nbLines();
        nbBytes();

        return number;
    }
    public void nbLines() {
        if (isLine()) {
            if (hasOption('n')) {
                number = Long.parseLong(getOptionValue('n'));
            } else {
                number = 10;
            }
        }
    }
    public void nbBytes() {
        if (!isLine()) {
            number = Long.parseLong(getOptionValue('b'));
        }
    }
    private boolean isLine() {
        String line = getLastPosition("lines", "bytes");
        if (line != null) {
            isLine = line.equals("lines");
        }
        else {
            isLine = true;
        }

        return isLine;
    }

    private Boolean isLine;
    private long number = -1;
}
