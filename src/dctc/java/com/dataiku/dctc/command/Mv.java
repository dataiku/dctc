package com.dataiku.dctc.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.dataiku.dctc.clo.Option;
import com.dataiku.dctc.command.abs.ListFilesCommand;
import com.dataiku.dctc.configuration.GlobalConf;
import com.dataiku.dctc.copy.CopyTask;
import com.dataiku.dctc.copy.CopyTasksExecutor;
import com.dataiku.dctc.copy.SimpleCopyTaskRunnableFactory;
import com.dataiku.dctc.display.ThreadedDisplay;
import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dip.utils.IndentedWriter;

public class Mv extends ListFilesCommand {
    public String tagline() {
        return "Move files and folders.";
    }
    public void longDescription(IndentedWriter printer) {
        printer.print("Move files and folders to another location.");
    }
    // Public
    @Override
    public String cmdname() {
        return "mv";
    }

    // Protected
    @Override
    public void execute(List<CopyTask> tasks) {
        if (getExitCode().getExitCode() == 0) {
            SimpleCopyTaskRunnableFactory fact = new SimpleCopyTaskRunnableFactory(false, false, false);
            ThreadedDisplay display = GlobalConf.getDisplay();
            CopyTasksExecutor exec = new CopyTasksExecutor(fact, display, getThreadLimit());

            try {
                exec.run(tasks, archive());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (exec.hasFail()) {
                setExitCode(2);
                exec.displayErrors();
            }
            else {
                // No errors, delete the directory if needed.
                for (GeneralizedFile dir: list) {
                    try {
                        if (!dir.delete()) {
                            error(dir, "Cannot delete directory", 2);
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected List<Option> setOptions() {
        List<Option> opts = new ArrayList<Option>();

        opts.add(stdOption('s', "sequential", "Make the copy with only one thread"));
        opts.add(stdOption('n', "thread-number", "Set the number of thread", true, "NUMBER"));

        return opts;
    }
    @Override
    protected boolean shouldAdd(GeneralizedFile src, GeneralizedFile dst, String root) {
        return true;
    }
    @Override
    protected String proto() {
        return "[OPT...] SOURCE... DST";
    }
    protected boolean recursion(GeneralizedFile dir) {
        return true;
    }
    @Override
    protected boolean includeLastPathElementInTarget() {
        return true;
    }
    protected boolean mkdirs(GeneralizedFile dst) {
        try {
            dst.mkdirs();
            return true;
        } catch (IOException e) {
            error(dst, e.getMessage(), 3);
            return false;
        }
    }
    protected void dstRoot(GeneralizedFile dst) {
    }
    protected void leave(GeneralizedFile sourceDir) {
        list.add(sourceDir);
    }
    @Override
    protected boolean deleteSource() {
        return true;
    }
    @Override
    protected boolean recursion() {
        return true;
    }

    // Attributes
    private List<GeneralizedFile> list = new ArrayList<GeneralizedFile>();
}
