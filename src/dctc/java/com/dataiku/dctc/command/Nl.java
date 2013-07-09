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
import com.dataiku.dip.utils.IntegerUtils;

public class Nl extends Command {
    public String cmdname() {
        return "nl";
    }
    public String proto() {
        return "[FILES]...";
    }
    public String tagline() {
        return "Numbered input files on the standard output.";
    }
    public List<Option> setOptions() {
        List<Option> opts = new ArrayList<Option>();

        opts.add(stdOption('i', "line-increment", "Line number increment at each line.", true)); // FIXME: NUMBER
        opts.add(stdOption('w', "number-width", "Use NUMBER columns for line numbers.", true)); // FIXME: NUMBER
        opts.add(stdOption('v', "starting-line", "First line number on each logical page.", true)); // FIXME: NUMBER

        return opts;
    }
    public void longDescription(IndentedWriter writer) {
        writer.print("Write each FILE to standard output, with line "
                     + "numbers added.  With no FILE, or when FILE is -, "
                     + "read standard input.");
    }
    public void perform(List<GeneralizedFile> args) {
        CatAlgorithmFactory fact = new CatAlgorithmFactory()
            .withAlgo(AlgorithmType.NL)
            .withLineIncrement(getIntOption('i', 1))
            .withIndentSeparator(" ")
            .withIndentSize(getIntOption('w', 6))
            .withStartingLine(getIntOption('v', 1));

        CatRunner runner = new CatRunner();
        runner.perform(args, false, fact, getExitCode());
    }

    // Private
    private int getIntOption(char option, int defaultValue) {
        if (hasOption(option)) {
            // FIXME: Should make a better management.
            return IntegerUtils.toInt(getOptionValue(option));
        }
        else {
            return defaultValue;
        }
    }
}
