package com.dataiku.dctc.command;

import static com.dataiku.dip.utils.PrettyString.scat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.clo.OptionAgregator;
import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dctc.configuration.GlobalConf;
import com.dataiku.dctc.copy.CopyTask;
import com.dataiku.dctc.copy.CopyTasksExecutor;
import com.dataiku.dctc.copy.SimpleCopyTaskRunnableFactory;
import com.dataiku.dctc.copy.SyncComputer;
import com.dataiku.dctc.copy.SyncComputer.IncrementalFilter.Type;
import com.dataiku.dctc.display.ThreadedDisplay;
import com.dataiku.dctc.file.GFile;
import com.dataiku.dip.utils.IndentedWriter;

public class Sync extends Command {
    public String tagline() {
        return "Synchronize incrementally the content of locations.";
    }
    public void longDescription(IndentedWriter printer) {
        printer.paragraph(scat("Synchronize locations. This command will copy"
                               , "all files from"
                               , "source to destination in order to have the"
                               , "destination contain"
                               , "all source files.")

                          ,scat("Files that are non-existing in destination"
                                , "are always copied. For"
                                , "files that already exist in the"
                                , "destination, behaviour depends on the"
                                , "-m and -t flags. Without them, files are"
                                , "copied if their size have"
                                , "changed")
                          ,scat("With -t, the modification time is taken into"
                                , "account. WARNING: dctc"
                                , "sync does not currently preserve mtimes,"
                                , "so you cannot use -t to"
                                , "re-sync already synced folders, it would"
                                , "always recopy all."
                                , "With -m, the MD5 hash is computed and"
                                , "files are copied if the MD5 hash"
                                , "does not match."));
   }

    // Public
    @Override
    public final String cmdname() {
        return "sync";
    }
    public Type getSyncType() {
        if (compress()) {
            return Type.EXISTS_ONLY;
        }
        if (hasOption('m')) {
            return Type.HASH_BASED;
        }
        if (hasOption('t')) {
            return Type.TIME_AND_SIZE;
        }
        return Type.SIZE_ONLY;
    }

    protected boolean compress() {
        return hasOption(GlobalConstants.COMPRESS_OPT);
    }

    @Override
    protected void setOptions(List<OptionAgregator> opts) {
        opts.add(stdOption('t'
                           , "time"
                           , "Check whether sync is required using file size"
                           + " and modification time (default: size only)."
                           + " Incompatible with -m"));
        opts.add(stdOption('m'
                           , "hash"
                           , "Check whether sync is required using file"
                           + " hash (default: size only). Incompatible"
                           + " with -t"));
        opts.add(stdOption('n'
                           , "dry-run"
                           , "Perform a trial run with no changes made."));
        opts.add(stdOption('c'
                           , "compress"
                           , "Compress the output files and appends .gz"
                           + " extension. Disables -t and -m"));
        opts.add(stdOption('s'
                           , "sequential"
                           , "Make the copy with only one thread."));
        opts.add(stdOption('n'
                           , "thread-number"
                           , "Set the number of thread."
                           , true
                           , "NUMBER"));
    }

    public void perform(List<GFile> args) {
        if (args.size() < 2) {
            usage();
            setExitCode(1);
            return;
        }

        SyncComputer computer = new SyncComputer(args.subList(0,
                                                              args.size() - 1)
                                                 , args.get(args.size() - 1));
        SyncComputer.IncrementalFilter filter
            = new SyncComputer.IncrementalFilter();
        filter.type = getSyncType();

        computer.compressTargets = compress();
        computer.recurseInSources = true;
        computer.filter = filter;

        try {
            List<CopyTask> tasks = computer.computeTasksList();
            if (hasOption('n')) {
                return;
            } else {
                execute(tasks);
            }
        } catch (FileNotFoundException e) {
            error("File not found: " + e.getMessage(), 1); // FIXME
        } catch (IOException e) {
            error("Failed to sync", e, 2);
        }
    }

    private void execute(List<CopyTask> tasks) throws IOException {
        System.out.println("Will copy "
                           + Integer.toString(tasks.size())
                           + " file(s).");
        if (tasks.size() == 0) {
            return;
        }
        SimpleCopyTaskRunnableFactory fact
            = new SimpleCopyTaskRunnableFactory(false // uncompress
                                                , false // compress
                                                , false // preserve date
                                                );
        ThreadedDisplay display = GlobalConf.getDisplay();

        CopyTasksExecutor exec = new CopyTasksExecutor(fact
                                                       , display
                                                       , getThreadLimit());
        exec.run(tasks, false);
        if (exec.hasFail()) {
            setExitCode(2);
            exec.displayErrors(getYell());
        }
    }
    @Override
    protected String proto() {
        return "[OPT...] INPUT... OUTPUT";
    }

    private int getThreadLimit() {
        int threadLimit = GlobalConf.getThreadLimit();
        if (hasOption('n')) {
            threadLimit = Integer.parseInt(getOptionValue('n').get(0));
        }
        if (hasOption('s')) {
            threadLimit = 1;
        }

        return threadLimit;
    }

}
