package com.dataiku.dctc.command;

import java.util.List;

import org.apache.commons.cli.Options;

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
    public Options setOptions() {
        Options opt = new Options();

        longOpt(opt, "Line number increment at each line."
                , "lineincrement", "i", "NUMBER");
        longOpt(opt, "Use NUMBER columns for line numbers."
                , "numberwidth", "w", "NUMBER");
        longOpt(opt, "First line number on each logical page."
                , "startingline", "v", "NUMBER");

        return opt;
    }
    public void longDescription(IndentedWriter writer) {
        writer.print("Write each FILE to standard output, with line "
                     + "numbers added.  With no FILE, or when FILE is -, "
                     + "read standard input.");
    }
    public void perform(List<GeneralizedFile> args) {
        CatAlgorithmFactory fact = new CatAlgorithmFactory()
            .withAlgo(AlgorithmType.NL)
            .withLineIncrement(getIntOption("i", 1))
            .withIndentSeparator(" ")
            .withIndentSize(getIntOption("w", 6))
            .withStartingLine(getIntOption("v", 1));

        CatRunner runner = new CatRunner();
        runner.perform(args, false, fact, getExitCode());
    }

    // Private
    private int getIntOption(String option, int defaultValue) {
        if (hasOption(option)) {
            // FIXME: Should make a better management.
            return IntegerUtils.toInt(getOptionValue(option));
        }
        else {
            return defaultValue;
        }
    }
}
