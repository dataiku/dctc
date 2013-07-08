package com.dataiku.dctc.command;

import java.util.List;

import org.apache.commons.cli.Options;

import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dctc.command.cat.AlgorithmType;
import com.dataiku.dctc.command.cat.CatAlgorithmFactory;
import com.dataiku.dctc.command.cat.CatRunner;
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
    protected Options setOptions() {
        Options opt = new Options();

        longOpt(opt, "Output the last K bytes", "bytes", "b", "K");
        longOpt(opt, "Output the last K lines", "lines", "n", "K");
        return opt;
    }
    @Override
    public void perform(List<GeneralizedFile> args) {
        CatAlgorithmFactory fact = new CatAlgorithmFactory()
            .withAlgo(AlgorithmType.TAIL)
            .withSkipFirst(number())
            .withIsLineAlgo(isLine());

        CatRunner runner = new CatRunner();

        runner.perform(args, true, fact, getExitCode());
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
            number = Long.parseLong(getOptionValue("b"));
        }
    }
    private boolean isLine() {
        if (isLine == null) {
            isLine = !hasOption("b");
        }

        return isLine;
    }

    private Boolean isLine;
    private long number = -1;
}
