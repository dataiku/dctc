package com.dataiku.dctc.command.abs;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.configuration.GlobalConf;
import com.dataiku.dctc.copy.CopyTask;
import com.dataiku.dctc.file.FileManipulation;
import com.dataiku.dctc.file.GeneralizedFile;

/**
 * Abstract class for listing commands.
 *
 * This class is based on GeneralizedFile.
 *
 * @see GeneralizedFile
 */
public abstract class ListCommand extends Command {
    // Abstract methods from Command class
    public abstract String cmdname();
    protected abstract String proto();

    // New abstract methods
    protected abstract boolean deleteSource();
    public abstract void execute(List<CopyTask> tasks, int exitCode) throws IOException;
    protected abstract boolean recursion(GeneralizedFile dir);
    protected abstract boolean shouldAdd(GeneralizedFile src, GeneralizedFile dst,
                                         String root) throws IOException;
    protected abstract void dstRoot(GeneralizedFile dst) throws IOException;
    protected abstract boolean includeLastPathElementInTarget();
    // Tell to the implementation that ListCommand leave `sourceDir'
    // directory.
    protected abstract void leave(GeneralizedFile sourceDir);

    @Override
    public void perform(String[] args) {
        List<GeneralizedFile> arguments = getArgs(args);
        if (earlyCheck(arguments)) {
            return;
        }
        if (arguments != null) {
            try {
                computeTasksList(arguments);
                execute(taskList, getExitCode());
            } catch (IOException e) {
                error("failed to " + cmdname(), e, 1);
            }
        }
    }

    public void performWithExceptions(List<GeneralizedFile> args) throws IOException {
        computeTasksList(args);
        if (getExitCode() != 0) {
            throw new IOException("Aborting "  + cmdname() + " due to previous errors");
        }
        execute(taskList, getExitCode());
    }

    public void perform(List<GeneralizedFile> args) {
        if (earlyCheck(args)) {
            return;
        }
        try {
            computeTasksList(args);
            if (getExitCode() != 0) {
                return;
            }
            execute(taskList, getExitCode());
        } catch (IOException e) {
            error("failed to " + cmdname(), e, 1);
        }
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
    private void computeTasksList(List<GeneralizedFile> args) throws IOException {
        GeneralizedFile[] sources = new GeneralizedFile[args.size() - 1];
        System.arraycopy(args.toArray(new GeneralizedFile[0]), 0, sources, 0, args.size() - 1);

        GeneralizedFile dst = args.get(args.size() - 1);

        if (checkArgs(sources, dst)) {
            logger.info("Abort copy : checkArgs");
            return;
        }

        dstRoot(dst);
        // src contains at least one element.
        for (GeneralizedFile source: sources) {
            logger.debug("Check " + source.getAbsoluteAddress());
            if (!source.exists()) {
                error(source.givenName(), "No such file or directory", 2);
                continue;
            }

            if (source.isDirectory()) {
                if (recursion(source)) {
                    List<GeneralizedFile> subfiles;
                    try {
                        subfiles = (List<GeneralizedFile>) source.grecursiveList();
                    } catch (IOException e) {
                        error(e.getMessage(), 1);
                        continue;
                    }
                    for (GeneralizedFile subfile: subfiles) {
                        String dstRoot;
                        if (subfile.givenName().equals(source.givenName())) {
                            continue;
                        }
                        dstRoot = FileManipulation.getSonPath(source.givenName(),
                                                              subfile.givenName(),
                                                              source.fileSeparator());
                        if (includeLastPathElementInTarget()
                            || dst.exists() || sources.length > 1) {
                            dstRoot = FileManipulation.concat(source.getFileName(), dstRoot, source.fileSeparator());
                        }
                        addQueue(subfile, dst, dstRoot);
                    }
                    leave(source);
                }
            } else {
                if (dst.isDirectory()
                    || sources.length > 1 || dst.givenName().endsWith(dst.fileSeparator())) {
                    addQueue(source, dst, source.getFileName());
                } else {
                    addQueue(source, dst, "");
                }
            }
        }
        return;
    }
    private boolean earlyCheck(List<GeneralizedFile> args) {
        if (args.size() < 2) {
            System.setOut(System.err);
            usage();
            return true;
        }
        return false;
    }
    private boolean checkArgs(GeneralizedFile[] src, GeneralizedFile dst) throws IOException {
        String prevSrcAddress = null;
        String dstAddress = dst.getAbsoluteAddress();
        java.util.Arrays.sort(src);
        for (int i = 0; i < src.length; ++i) {
            String srcAddress = src[i].getAbsoluteAddress();

            if (srcAddress.equals(dstAddress)) {
                error("`" + srcAddress + "' and `" + dstAddress + "' are the same file.", 1);
                return true;
            }
            if (i != 0 && prevSrcAddress.equals(srcAddress)) {
                error("source file ‘" + srcAddress + "’ specified more than once.", 2);
                return true;
            }
            prevSrcAddress = srcAddress;
        }
        if (dst.exists() && !dst.isDirectory()) {
            // src has at least, one element (checked by earlyCheck()).
            if (src.length > 1 && !dst.isDirectory() && !archive()) {
                error(dstAddress, "is not a directory or the destination is not compressed.", 2);
                return true;
            }
        }
        return false;
    }
    private void addQueue(GeneralizedFile src, GeneralizedFile dst, String root) throws IOException {
        if (compress() && !root.endsWith(".gz")) {
            root += ".gz";
        }
        if (uncompress() && root.endsWith(".gz")) {
            root = root.substring(0, root.length() - 3);
        }
        if (shouldAdd(src, dst, root)) {
            taskList.add(new CopyTask(src, dst, root, deleteSource()));
        } else {
            logger.info("Should not add");
        }
    }

    protected int getThreadLimit() {
        int threadLimit = GlobalConf.getThreadLimit();
        if (hasOption("n")) {
            threadLimit = Integer.parseInt(getOptionValue("n"));
        }
        if (hasOption("s")) {
            threadLimit = 1;
        }

        return threadLimit;
    }

    // Attributes
    private List<CopyTask> taskList = new ArrayList<CopyTask>();
    private static Logger logger = Logger.getLogger("dctc.command");
}
