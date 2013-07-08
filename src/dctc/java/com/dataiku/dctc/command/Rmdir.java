package com.dataiku.dctc.command;

import static com.dataiku.dip.utils.PrettyString.scat;

import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.Options;

import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dip.utils.IndentedWriter;

public class Rmdir extends Command {
    public String tagline() {
        return "Remove empty folders.";
    }
    public void longDescription(IndentedWriter printer) {
        printer.print(scat("Remove empty folders. If the target folder is not empty,"
                           ,"rmdir will error out."));
    }
    @Override
    public void perform(List<GeneralizedFile> args) {
        for (GeneralizedFile arg: args) {
            try {
                if (!arg.exists()) {
                    error(arg, "No such file or directory", 2);
                    continue;
                }
                else if (!arg.isEmpty()) {
                    error(arg, "Directory not empty", 2);
                    continue;
                }
                arg.delete();
                if (hasOption("v")) {
                    System.out.println("rmdir: removing directory, `" + arg.givenName() + "'");
                }
            }
            catch (IOException e) {
                error("failed to rmdir " + arg.givenName(), e, 0);
            }
        }
    }
    @Override
    public String cmdname() {
        return "rmdir";
    }

    @Override
    protected Options setOptions() {
        Options options = new Options();
        return options;
    }
    @Override
    protected String proto() {
        return "[OPT...] [FILES...]";
    }
}
