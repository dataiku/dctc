package com.dataiku.dctc.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Options;

import com.dataiku.dctc.configuration.GlobalConf;
import com.dataiku.dctc.copy.CopyTask;
import com.dataiku.dctc.copy.CopyTasksExecutor;
import com.dataiku.dctc.copy.SimpleCopyTaskRunnableFactory;
import com.dataiku.dctc.display.ThreadedDisplay;
import com.dataiku.dctc.file.GeneralizedFile;

public class Mv extends ListCommand {
    public String tagline() {
        return "Move files and folders";
    }
    public String longDescription() {
        return "Move files and folders to another location.";
    }
    // Public
    @Override
    public String cmdname() {
        return "mv";
    }

    // Protected
    @Override
    public void execute(List<CopyTask> tasks, int exitCode) throws IOException {
        if (exitCode == 0) {
            SimpleCopyTaskRunnableFactory fact = new SimpleCopyTaskRunnableFactory(false, false, false);
            ThreadedDisplay display = GlobalConf.getDisplay();
            CopyTasksExecutor exec = new CopyTasksExecutor(fact, display, GlobalConf.getThreadLimit());

            exec.run(tasks, archive());
            if (exec.hasFail()) {
                setExitCode(2);
                exec.displayErrors();
            }
            else {
                // No errors, delete the directory if needed.
                for (GeneralizedFile dir: list) {
                    if (!dir.delete()) {
                        error(dir.givenName(), "Cannot delete directory", 2);
                    }
                }
            }
        }
    }

    @Override
    protected Options setOptions() {
        Options options = new Options();
        return options;
    }
    @Override
    protected boolean shouldAdd(GeneralizedFile src, GeneralizedFile dst, String root) throws IOException {
        return true;
    }
    @Override
    protected String proto() {
        return "dctc mv [OPT...] SOURCE... DST";
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
            error(dst.givenName(), e.getMessage(), 3);
            return false;
        }
    }
    protected void dstRoot(GeneralizedFile dst) throws IOException {
    }
    protected void leave(GeneralizedFile sourceDir) {
        list.add(sourceDir);
    }
    @Override
    protected boolean deleteSource() {
        return true;
    }

    // Attributes
    private List<GeneralizedFile> list = new ArrayList<GeneralizedFile>();
}
