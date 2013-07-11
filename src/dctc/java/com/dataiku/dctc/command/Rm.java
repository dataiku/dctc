package com.dataiku.dctc.command;

import static com.dataiku.dip.utils.PrettyString.scat;

import java.io.IOException;
import java.util.List;

import com.dataiku.dctc.clo.Option;
import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dctc.display.Interactive;
import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dip.utils.IndentedWriter;

public class Rm extends Command {
    public String tagline() {
        return "Remove files and folders.";
    }
    public void longDescription(IndentedWriter printer) {
        printer.print(scat("Remove files and folders. By default, it will refuse to remove"
                           , "folders. Use -r"));
    }
    // Public
    @Override
    public void perform(List<GeneralizedFile> args) {

        for (GeneralizedFile arg: args) {
            try {
                if (exists(arg) && couldDelDir(arg)) {
                    rm(arg);
                }
            } catch (IOException e) {
                error(arg, "failed to delete", e, 0);
            }
        }
    }
    @Override
    public String cmdname() {
        return "rm";
    }
    /// Getters
    public boolean interactive() {
        if (interactive == null) {
            interactive = hasOption('i');
        }
        return interactive;
    }
    public boolean verbose() {
        if (verbose == null) {
            verbose = hasOption('v');
        }
        return verbose;
    }
    public boolean recursiveDeletion() {
        if (recursiveDeletion == null) {
            recursiveDeletion = hasOption('r');
        }
        return recursiveDeletion;
    }
    public boolean force() {
        if (force == null) {
            force = hasOption('f');
        }
        return force;
    }
    /// Setters
    public Rm interactive(boolean interactive) {
        this.interactive = interactive;
        return this;
    }
    public Rm verbose(boolean verbose) {
        this.verbose = verbose;
        return this;
    }
    public Rm recursiveDeletion(boolean recursiveDeletion) {
        this.recursiveDeletion = recursiveDeletion;
        return this;
    }
    public Rm force(boolean force) {
        this.force = force;
        return this;
    }

    // Protected
    @Override
    protected void setOptions(List<Option> opts) {
        opts.add(stdOption('v', "verbose", "Explain what i being done."));
        opts.add(stdOption("rR", "recursive", "Remove directories and their contents recursively."));
        opts.add(stdOption('f', "force", "Ignore nonexistent files and argumens, never prompt."));
        opts.add(stdOption('i', "Prompt before every removal."));
    }
    @Override
    protected String proto() {
        return "[OPT...] [FILE...]";
    }

    // Private
    private void del(GeneralizedFile arg) throws IOException {
        if (recursiveDeletion() || arg.isFile() || arg.isEmpty()) {
            if (!arg.delete()) {
                if (!force()) {
                    error(arg, "Cannot remove", 2);
                }
            }
        }
        else {
            notEmpty(arg);
        }
    }
    private boolean dirAsk(GeneralizedFile arg) throws IOException {
        if (arg.isEmpty()) {
            return Interactive.ask("rm", "rm: remove directory `" + arg.givenName() + "'? ",
                                   "yY", "nN");
        }
        else {
            return Interactive.ask("rm", "rm: descend into directory `" + arg.givenName() + "'? ",
                                   "yY", "nN");
        }
    }
    private boolean fileAsk(GeneralizedFile arg) {
        return Interactive.ask("rm", "rm: remove regular file `" + arg.givenName() + "'? ",
                               "yY", "nN");
    }
    private void notEmpty(GeneralizedFile arg) {
        error(arg, "Cannot remove, directory not empty", 1);
    }
    private void rm(GeneralizedFile arg) throws IOException {
        if (arg.isDirectory()) {
            if (!interactive() || dirAsk(arg)) {
                if (interactive()) {
                    // If one file is not delete, we must not delete
                    // the root.
                    List<? extends GeneralizedFile> sons = arg.grecursiveList();
                    for (int i = sons.size() - 1; i != -1; --i) {
                        GeneralizedFile son = sons.get(i);
                        if (son.isDirectory()) {
                            if (dirAsk(son)) {
                                del(son);
                            }
                        }
                        else {
                            if (fileAsk(son)) {
                                del(son);
                            }
                        }
                    }
                }
                else {
                    if (verbose()) {
                        @SuppressWarnings("unchecked")
                        List<GeneralizedFile> rlist = (List<GeneralizedFile>) arg.grecursiveList();
                        for (int i = rlist.size() - 1; i != -1; --i) {
                            GeneralizedFile son = rlist.get(i);
                            verbose(son.givenName());
                            del(son);
                        }
                    }
                    else {
                        del(arg);
                    }
                }
            }
        }
        else {
            if (!interactive() || fileAsk(arg)) {
                del(arg);
            }
        }
    }
    private boolean couldDelDir(GeneralizedFile arg) throws IOException {
        if (arg.isDirectory() && !recursiveDeletion()) {
            if (!force()) {
                error(arg, "Cannot remove, is a directory", 2);
            }
            return false;
        }
        return true;
    }
    private boolean exists(GeneralizedFile arg) throws IOException {
        if (arg.exists() || force()) {
            return true;
        }
        else {
            if (!force()) {
                error(arg, "Cannot remove, no such file or directory", 2);
            }
            return false;
        }
    }
    private void verbose(String arg) {
        if (verbose()) {
            System.out.println("removed `" + arg + "'");
        }
    }

    // Attributes
    private Boolean interactive = null;
    private Boolean verbose = null;
    private Boolean recursiveDeletion = null;
    private Boolean force = null;
}
