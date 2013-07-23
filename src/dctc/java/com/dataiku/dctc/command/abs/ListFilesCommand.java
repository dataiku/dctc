package com.dataiku.dctc.command.abs;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.configuration.GlobalConf;
import com.dataiku.dctc.copy.CopyTask;
import com.dataiku.dctc.file.FileManipulation;
import com.dataiku.dctc.file.GFile;

public abstract class ListFilesCommand extends Command {
    // Abstract methods from Command class
    public abstract String cmdname();
    protected abstract String proto();

    // New abstract methods
    protected abstract boolean deleteSource();
    public abstract void execute(List<CopyTask> tasks);
    protected abstract boolean recursion();
    protected abstract boolean shouldAdd(GFile src, GFile dst, String root);
    protected abstract void dstRoot(GFile dst);
    protected abstract boolean includeLastPathElementInTarget();
    // Tell to the implementation that ListCommand leave `sourceDir'
    // directory.
    protected abstract void leave(GFile sourceDir);

    @Override
    public void perform(String[] args) {
        List<GFile> arguments = getArgs(args);

        // Check if enough argument
        if (arguments.size() < 2) {
            setExitCode(2);
            System.setOut(System.err);
            usage();

            return;
        }

        computeTasksList(arguments);
        execute(taskList);
    }

    public void perform(List<GFile> args) {
        if (args.size() < 2) {
            setExitCode(1);
            System.setOut(System.err);
            usage();
            return;
        }

        computeTasksList(args);
        execute(taskList);

    }
    public boolean compress() {
        return hasOption(GlobalConstants.COMPRESS_OPT);
    }
    public boolean uncompress() {
        return hasOption(GlobalConstants.UNCOMPRESS_OPT);
    }
    public boolean archive() {
        return hasOption(GlobalConstants.ARCHIVE_OPT);
    }
    public boolean unarchive() {
        return hasOption(GlobalConstants.UNARCHIVE_OPT);
    }

    @SuppressWarnings("unchecked")
    private void computeTasksList(List<GFile> args) {
        GFile[] sources = getSources(args);
        GFile dst = args.get(args.size() - 1);

        if (!checkArgs(sources, dst)) {
            return;
        }

        boolean dstIsDirectory; {
            try {
                dstIsDirectory = dst.isDirectory();
            }
            catch (IOException e) {
                unexpected(dst, e);
                return;
            }
        }
        boolean mergeRoot = sources.length == 1;

        dstRoot(dst);
        // src contains at least one element.
        for (GFile source: sources) {
            // If not exists, continue.
            try  {
                if (!source.exists()) {
                    noSuch(source);
                    continue;
                }
            }
            catch (IOException e) {
                noSuch(source);
            }

            boolean isDirectory; { // Source is a directory?
                try {
                    isDirectory = source.isDirectory();
                }
                catch (IOException e) {
                    unexpected(source, e);
                    continue;
                }
            }

            if (isDirectory) {
                if (!recursion()) {
                    ommit(source);
                    continue;
                }
                List<GFile> subfiles; {
                    try {
                         subfiles = (List<GFile>) source.grecursiveList();
                    }
                    catch (IOException e) {
                        unexpected(source, e);
                        continue;
                    }
                }

                for (GFile subfile: subfiles) {
                    if (subfile.givenName().equals(source.givenName())) {
                        // subfiles contains the source directory.
                        continue;
                    }

                    addQueue(source, subfile, dst, mergeRoot);
                }
                leave(source);
            }
            else { // is a file
                addQueue(source, source, dst, mergeRoot && !dstIsDirectory);
            }
        }
    }
    private GFile[] getSources(List<GFile> args) {
        GFile[] sources = new GFile[args.size() - 1];
        System.arraycopy(args.toArray(new GFile[0]), 0, sources, 0, args.size() - 1);

        return sources;
    }
    private void noSuch(GFile file) {
        error(file, "No such file or directory", 2);
    }
    private void unexpected(GFile file, Throwable  e) {
        error(file, "Unexpected error", e, 2);
    }
    private void ommit(GFile dir) {
        error(dir, "Ommit directory", 2);
    }
    private boolean checkArgs(GFile[] src, GFile dst) {
        String dstAddress = dst.getAbsoluteAddress();
        java.util.Arrays.sort(src);
        for (int i = 0; i < src.length; ++i) {
            String srcAddress = src[i].getAbsoluteAddress();

            if (srcAddress.equals(dstAddress)) {
                error("`" + srcAddress + "' and `" + dstAddress + "' are the same file.", 1);
                return false;
            }
        }
        // FIXME: Check if same files are present in the line command.
        // FIXME: Wasn't detected: `dctc cp foo bar foo lol`.
        try {
            if (dst.exists() && !dst.isDirectory()) {
                // src has at least, one element (checked by earlyCheck()).
                if (src.length > 1 && !dst.isDirectory() && !archive()) {
                    error(dst, "is not a directory or the destination is not compressed.", 2);
                    return false;
                }
            }
        }
        catch (IOException e) {
            // Only dst needs to throw here.
            error(dst, "Unexpected error", e, 3);
            return false;
        }

        return true;
    }
    private void addQueue(GFile root, GFile src,
                          GFile dst, boolean mergeRoot) {
        String dstRoot = FileManipulation.getSonPath(root.givenName(), src.givenName(), root.fileSeparator());

        if (!mergeRoot) {
            dstRoot = FileManipulation.concat(root.getFileName(), dstRoot, root.fileSeparator());
        }

        addQueue(src, dst, dstRoot);
    }
    private void addQueue(GFile src, GFile dst, String root) {
        if (compress() && !root.endsWith(".gz")) {
            root += ".gz";
        }
        else if (uncompress() && root.endsWith(".gz")) {
            root = root.substring(0, root.length() - 3);
        }
        if (shouldAdd(src, dst, root)) {
            taskList.add(new CopyTask(src, dst, root, deleteSource()));
        }
    }

    protected int getThreadLimit() {
        int threadLimit = GlobalConf.getThreadLimit();
        if (hasOption('n')) {
            threadLimit = Integer.parseInt(getOptionValue('n'));
        }
        if (hasOption('s')) {
            threadLimit = 1;
        }

        return threadLimit;
    }

    // Attributes
    private List<CopyTask> taskList = new ArrayList<CopyTask>();
}
