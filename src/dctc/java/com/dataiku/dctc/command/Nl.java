package com.dataiku.dctc.command;

import java.util.List;

import org.apache.commons.cli.Options;

import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dctc.command.cat.AlgorithmType;
import com.dataiku.dctc.command.cat.CatAlgorithmFactory;
import com.dataiku.dctc.command.cat.CatRunner;
import com.dataiku.dctc.file.GeneralizedFile;
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
    public Options setOptions() {
        return new Options();
    }
    public void longDescription(IndentedWriter writer) {
        writer.print("Write each FILE to standard output, with line "
                     + "numbers added.  With no FILE, or when FILE is -, "
                     + "read standard input.");
    }
    public void perform(List<GeneralizedFile> args) {
        CatAlgorithmFactory fact = new CatAlgorithmFactory()
            .withAlgo(AlgorithmType.NL);

        CatRunner runner = new CatRunner();
        runner.perform(args, false, fact, getExitCode());
    }
}
