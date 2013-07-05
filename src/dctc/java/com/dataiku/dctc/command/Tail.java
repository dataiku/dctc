package com.dataiku.dctc.command;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;

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
        if (nbByte() != -1) {
            performByte(args);
        } else {
            performLine(args);
        }
    }
    private void performLine(List<GeneralizedFile> args) {
        CatAlgorithmFactory fact = new CatAlgorithmFactory()
            .withAlgo(AlgorithmType.TAIL)
            .withNbLine((int) nbLine()); // FIXME: Pass the number of line to int

        CatRunner runner = new CatRunner();

        runner.perform(args, true, fact, getExitCode());
    }
    private void performByte(List<GeneralizedFile> args) {
        for (GeneralizedFile arg: args) {
            if (args.size() > 1) {
                header(arg);
            }
            if (arg.canGetPartialFile()) {
                try {
                    IOUtils.copyLarge(arg.getLastBytes(nbByte()), System.out);
                } catch (IOException e) {
                    error(arg.givenName(), e.getMessage(), 1);
                }
            } else {
                try {
                    InputStream input = arg.inputStream();
                    long toSkip = arg.getSize() - nbByte();

                    while (toSkip > 0) {
                        toSkip -= input.skip(toSkip);
                    }

                    IOUtils.copyLarge(input, System.out);
                } catch (IOException e) {
                    error(arg.givenName(), e.getMessage(), 1);
                }
            }
        }
    }
    private void header(GeneralizedFile arg) {
        System.out.print("===> ");
        System.out.print(arg.givenName());
        System.out.println(" <===");
    }
    public int nbLine() {
        if (nbLine == -1) {
            if (hasOption("line")) {
                nbLine = Integer.parseInt(getOptionValue("line"));
            } else {
                nbLine = 10;
            }
        }
        return nbLine;
    }
    public Tail nbLine(int nbLine) {
        this.nbLine = nbLine;
        return this;
    }
    public int nbByte() {
        if (nbByte == -1) {
            if (hasOption("byte")) {
                nbByte = Integer.parseInt(getOptionValue("byte"));
            }
        }
        return nbByte;
    }
    public Tail nbByte(int nbByte) {
        this.nbByte = nbByte;
        return this;
    }

    private int nbLine = -1;
    private int nbByte = -1;
}
