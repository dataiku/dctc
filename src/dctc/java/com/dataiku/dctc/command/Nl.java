package com.dataiku.dctc.command;

import java.util.List;

import com.dataiku.dctc.clo.OptionAgregator;
import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dctc.command.cat.AlgorithmType;
import com.dataiku.dctc.command.cat.CatAlgorithmFactory;
import com.dataiku.dctc.command.cat.CatRunner;
import com.dataiku.dctc.command.cat.NeverCatHeaderSelector;
import com.dataiku.dctc.file.GFile;
import com.dataiku.dip.utils.IndentedWriter;

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
    public void setOptions(List<OptionAgregator> opts) {
        opts.add(stdOption('i', "line-increment"
                           , "Line number increment at each line."
                           , true
                           , "NUMBER"));
        opts.add(stdOption('w', "number-width"
                           , "Use NUMBER columns for line numbers."
                           , true
                           , "NUMBER"));
        opts.add(stdOption('v', "starting-line"
                           , "First line number on each logical page."
                           , true
                           , "NUMBER"));
    }
    public void longDescription(IndentedWriter writer) {
        writer.print("Write each FILE to standard output, with line"
                     + " numbers added.  With no FILE, or when FILE is -,"
                     + " read standard input.");
    }
    public void perform(List<GFile> args) {
        CatAlgorithmFactory fact = new CatAlgorithmFactory()
            .withAlgo(AlgorithmType.NL)
            .withLineIncrement(getIntOption('i', 1))
            .withIndentSeparator(" ")
            .withIndentSize(getIntOption('w', 6))
            .withStartingLine(getIntOption('v', 1))
            .withYell(getYell());

        if (getExitCode().getExitCode() != 0) {
            return;
        }

        CatRunner runner = new CatRunner()
            .withHeader(new NeverCatHeaderSelector());

        runner.perform(args, fact, getExitCode(), false);
    }
}
