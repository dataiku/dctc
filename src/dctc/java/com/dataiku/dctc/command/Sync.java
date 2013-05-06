package com.dataiku.dctc.command;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.Options;

import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.configuration.GlobalConf;
import com.dataiku.dctc.copy.CopyTask;
import com.dataiku.dctc.copy.CopyTasksExecutor;
import com.dataiku.dctc.copy.SimpleCopyTaskRunnableFactory;
import com.dataiku.dctc.copy.SyncComputer;
import com.dataiku.dctc.copy.SyncComputer.IncrementalFilter.Type;
import com.dataiku.dctc.display.ThreadedDisplay;
import com.dataiku.dctc.file.GeneralizedFile;

public class Sync extends Command {
    public String tagline() {
        return "Synchronize incrementally the content of locations";
    }
    public String longDescription() {
        return "Synchronize locations. This command will copy all files from " +
              "source to destination in order to have the destination contain " +
              "all source files.\n"+
              "\n"+
              "Files that are non-existing in destination are always copied. For\n"+
              "files that already exist in the destination, behaviour depends on the\n"+
              "-m and -t flags. Without them, files are copied if their size have changed\n"+
              "\n"+
              "With -t, the modification time is taken into account. WARNING: dctc sync\n"+
              "does not currently preserve mtimes, so you cannot use -t to re-sync already\n"+
              "synced folders, it would always recopy all.\n"+
              "With -m, the MD5 hash is computed and files are copied if the MD5 hash \n"+
              "does not match.";
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
        if (hasOption("m")) {
            return Type.HASH_BASED;
        }
        if (hasOption("t")) {
            return Type.TIME_AND_SIZE;
        }
        return Type.SIZE_ONLY;
    }

    protected boolean compress() {
        return hasOption(GlobalConstants.COMPRESS_OPT);
    }

    @Override
    protected Options setOptions() {
        Options options = new Options();

        options.addOption("t", "time", false, "Check whether sync is required using file size and modification time (default: size only). Incompatible with -m");
        options.addOption("m", "hash", false, "Check whether sync is required using file hash (default: size only). Incompatible with -t");
        options.addOption("n", "dry-run", false, "Perform a trial run with no changes made.");
        options.addOption("c", "compress", false, "Compress the output files and appends .gz extension. Disables -t and -m");

        return options;
    }

    public void perform(List<GeneralizedFile> args) {
        if (args.size() < 2) {
            usage();
            exitCode(1);
            return;
        }

        SyncComputer computer = new SyncComputer(args.subList(0, args.size() - 1), args.get(args.size() - 1));
        SyncComputer.IncrementalFilter filter = new SyncComputer.IncrementalFilter();
        filter.type = getSyncType();

        computer.compressTargets = compress();
        computer.recurseInSources = true;
        computer.filter = filter;

        try {
            List<CopyTask> tasks = computer.computeTasksList();
            if (hasOption("n")) {
                return;
            } else {
                execute(tasks);
            }
        } catch (FileNotFoundException e) {
            error("File not found : " + e.getMessage(), 1);
        } catch (IOException e) {
            error("Failed to sync", e, 2);
        }
    }

    private void execute(List<CopyTask> tasks) throws IOException {
        System.out.println("Will copy " + Integer.toString(tasks.size()) + " file(s).");
        if (tasks.size() == 0) {
            return;
        }
        SimpleCopyTaskRunnableFactory fact = new SimpleCopyTaskRunnableFactory(false /*uncompress*/, false /*compress*/);
        ThreadedDisplay display = GlobalConf.getDisplay();
        CopyTasksExecutor exec = new CopyTasksExecutor(fact, display, GlobalConf.getThreadLimit());
        exec.run(tasks, false);
        if (exec.hasFail()) {
            exitCode(2);
            exec.displayErrors();
        }
    }
    @Override
    protected String proto() {
        return "dctc sync [OPT...] INPUT... OUTPUT: synchronizes input folders into output";
    }
}
