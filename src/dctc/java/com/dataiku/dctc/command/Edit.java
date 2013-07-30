package com.dataiku.dctc.command;

import static com.dataiku.dip.utils.PrettyString.scat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.dataiku.dctc.clo.OptionAgregator;
import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dctc.copy.CopyTask;
import com.dataiku.dctc.copy.SimpleCopyTaskRunnableFactory;
import com.dataiku.dctc.file.GFile;
import com.dataiku.dctc.file.LocalFile;
import com.dataiku.dctc.file.PathManip;
import com.dataiku.dip.utils.DKUtils;
import com.dataiku.dip.utils.IndentedWriter;

public class Edit extends Command {
    public String tagline() {
        return "Edit a remote file.";
    }
    public void longDescription(IndentedWriter printer) {
        printer.print(scat("The file is downloaded, an editor is opened, and if the file was"
                           ,"modified, it's uploaded back to its original location"));
    }

    @Override
    public String cmdname() {
        return "edit";
    }
    public String getEditor() {
        if (hasOption("-editor")) {
            return getOptionValue("-editor").get(0);
        }
        else {
            return System.getenv("EDITOR");
        }
    }

    @Override
    public void perform(List<GFile> files) {
        List<CopyTask> tasks = new ArrayList<CopyTask>();
        List<GFile> localFiles = new ArrayList<GFile>();

        if (files.size() == 0) {
            usage();
            setExitCode(1);
            return;
        }

        String rawEditor = getEditor();
        if (rawEditor == null) {
            error("The variable `EDITOR' is not set in your environment.", 1);
            return;
        }
        for (int i = 0; i < files.size(); i++) {
            GFile f = files.get(i);
            if (f instanceof LocalFile) {
                localFiles.add(f);
            }
            else {
                String fileName = f.getFileName() + "-tmp";
                String extension = PathManip.extension(fileName);
                if (extension.isEmpty()) {
                    extension = "empty";
                }

                File output;
                try {
                    output = File.createTempFile(fileName, "." + extension);
                }
                catch (IOException e) {
                    error(f, "Cannot create a local temporary file", e, 3);
                    return;
                }
                tasks.add(new CopyTask(f, new LocalFile(output.getAbsolutePath()), "", false));
            }
        }

        List<Long> timestamps = new ArrayList<Long>();
        if (!copy(tasks)) {
            return;
        }
        for (CopyTask task: tasks) {
            try {
                timestamps.add(task.dstDir.getDate());
            }
            catch (IOException e) {
                error(task.dstDir, "Could not get the file date, will always be copied back.", e, 0);
                timestamps.add(-51L);
            }
        }

        /* Build command-line and execute editor */
        List<String> args = new ArrayList<String>();
        for (String c: rawEditor.split(" ")) {
            args.add(c);
        }
        for (CopyTask task: tasks) {
            args.add(task.dstDir.getAbsolutePath());
        }
        for (GFile file: localFiles) {
            args.add(file.givenName());
        }
        String[] cmdArgs; {
            if (new File("/dev/tty").exists()) {
                cmdArgs = new String[] {"sh", "-c", "'"
                                        + StringUtils.join(args, "' '")
                                        + "' > /dev/tty < /dev/tty"};

            }
            else {
                cmdArgs = args.toArray(new String[0]);
            }
        }
        try {
            DKUtils.execAndGetOutput(cmdArgs, null);
        }
        catch (IOException e) {
            error("Unexpected error", e, 3);
        }
        catch (InterruptedException e) {
            error("Unexpected interruption", e, 3);
        }

        // Compute files that must be reuploaded
        List<CopyTask> backTasks = new ArrayList<CopyTask>();
        for (int i = 0; i < tasks.size(); i++) {
            long oldTS = timestamps.get(i);
            CopyTask task = tasks.get(i);
            long newTS; {
                try {
                    newTS= task.dstDir.getDate();
                }
                catch (IOException e) {
                    error(task.dstDir, "Could not get the file date, will always be copied back.", e, 0);
                    newTS = -42;
                }
            }
            if (oldTS != newTS) {
                backTasks.add(new CopyTask(task.dstDir, task.src, "", false));
            }
        }
        if (!copy(backTasks)) {
            return;
        }

        // Delete temporary files
        for (CopyTask task: tasks) {
            try {
                task.dstDir.delete();
            }
            catch (IOException e) {
                error(task.dstDir, "Cannot delete the file", e, 3);
            }
        }
    }

    // Protected
    @Override
    protected void setOptions(List<OptionAgregator> opts) {
        opts.add(stdOption('e', "editor", "Override the EDITOR variable.", true, "BIN-NAME"));
    }

    @Override
    protected String proto() {
        return "PATH...";
    }

    // Private
    private boolean copy(List<CopyTask> tasks) {
        SimpleCopyTaskRunnableFactory fact = new SimpleCopyTaskRunnableFactory(false, false, true);

        for (CopyTask task : tasks) {
            try {
                fact.build(task).work();
            }
            catch (IOException e) {
                error(task.dstDir, "Unexpected error", e, 3);
                return false;
            }
        }
        return true;
    }
}
