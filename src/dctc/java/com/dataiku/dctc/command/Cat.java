package com.dataiku.dctc.command;

import static com.dataiku.dip.utils.PrettyString.scat;

import java.util.List;

import org.apache.commons.cli.Options;

import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dctc.command.cat.CatAlgorithm;
import com.dataiku.dctc.command.cat.CatAlgorithmFactory;
import com.dataiku.dctc.command.cat.AlgorithmType;
import com.dataiku.dctc.file.GeneralizedFile;
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
    public void perform(List<GeneralizedFile> args) {
        // Set the option to the factory.
        fact = new CatAlgorithmFactory()
            .withAlgo(AlgorithmType.CAT)
            .withLinum(hasOption("n"))
            .withDollar(hasOption("E"));

        for (GeneralizedFile arg: args) {
            run(arg);
        }
    }
    @Override
    public final String cmdname() {
        return "cat";
    }
    // Protected
    @Override
    protected Options setOptions() {
        Options options = new Options();

        options.addOption("n", "number", false, "Number all output lines.");
        options.addOption("E", "show-ends", false, "Display $ at end of each line.");
        options.addOption("s", "squeeze-blank", false, "Suppress repeated empty output lines.");

        return options;
    }
    @Override
    protected final String proto() {
        return "[OPT...] FILES...";
    }

    private void run(GeneralizedFile file) {
        // Run cat.
        CatAlgorithm algo = fact.build(file);

        algo.run();
        setExitCode(algo.getExitCode());
    }

    // Attributes
    CatAlgorithmFactory fact;
    CatAlgorithm cat;
}
