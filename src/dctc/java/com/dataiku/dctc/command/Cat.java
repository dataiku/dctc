package com.dataiku.dctc.command;

import static com.dataiku.dip.utils.PrettyString.scat;

import java.util.List;

import com.dataiku.dctc.clo.OptionAgregator;
import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dctc.command.cat.AlgorithmType;
import com.dataiku.dctc.command.cat.CatAlgorithm;
import com.dataiku.dctc.command.cat.CatAlgorithmFactory;
import com.dataiku.dctc.command.cat.CatRunner;
import com.dataiku.dctc.command.cat.NeverCatHeaderSelector;
import com.dataiku.dctc.file.GFile;
import com.dataiku.dip.utils.IndentedWriter;

public class Cat extends Command {
    public String tagline() {
        return "Display the content of one or several files.";
    }
    public void longDescription(IndentedWriter printer) {
        printer.print(scat("Concatenates the content of all input files to standard output."
                           ,"GZip compressed files are automatically uncompressed"));
    }

    // Public
    @Override
    public void perform(List<GFile> args) {
        // Set the option to the factory.
        fact = new CatAlgorithmFactory()
            .withAlgo(AlgorithmType.CAT)
            .withLinum(hasOption('n'))
            .withDollar(hasOption('E')
                        || hasOption('e'))
            .withYell(getYell())
            .withPrettyChar(hasOption("-show-nonprinting")
                            || hasOption('t')
                            || hasOption('e'))
            .withShowTabulation(hasOption("-show-tabs")
                                || hasOption('t'));

        CatRunner runner = new CatRunner()
            .withHeader(new NeverCatHeaderSelector())
            .withYell(getYell());
        runner.perform(args, fact, getExitCode(), false);
    }
    @Override
    public final String cmdname() {
        return "cat";
    }
    // Protected
    @Override
    protected void setOptions(List<OptionAgregator> options) {
        options.add(stdOption('n', "number", "Number all output lines."));
        options.add(stdOption('E', "show-ends", "Display $ at end of each line."));
        options.add(stdOption('s', "squeeze-blank", "Suppress repeated empty output lines."));
        options.add(stdOption('v', "show-nonprinting", "Use ^ and M- notation exception for LFD and TAB."));
        options.add(stdOption('T', "show-tabs", "display TAB characters as ^I"));
        options.add(stdOption('t', "Equivalent to -vT"));
        options.add(stdOption('e', "Equivalent to -vE"));
    }
    @Override
    protected final String proto() {
        return "[OPT...] FILES...";
    }

    // Attributes
    CatAlgorithmFactory fact;
    CatAlgorithm cat;
}
