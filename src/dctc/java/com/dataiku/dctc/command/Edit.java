package com.dataiku.dctc.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Options;
import org.apache.commons.lang.StringUtils;

import com.dataiku.dctc.copy.CopyTask;
import com.dataiku.dctc.copy.SimpleCopyTaskRunnableFactory;
import com.dataiku.dctc.file.FileManipulation;
import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dctc.file.LocalFile;
import com.dataiku.dip.utils.DKUtils;
import com.dataiku.dip.utils.IndentedWriter;

import static com.dataiku.dip.utils.PrettyString.scat;

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
        if (hasOption("e")) {
            return getOptionValue("e");
        } else {
            return System.getenv("EDITOR");
        }
    }

    @Override
    public void perform(List<GeneralizedFile> files) {
        List<CopyTask> tasks = new ArrayList<CopyTask>();
        List<GeneralizedFile> localFiles = new ArrayList<GeneralizedFile>();
        String rawEditor = getEditor();
        if (rawEditor == null) {
            error("The variable `EDITOR' is not set in your environment.", 1);
            return;
        }
        if (files.size() == 0) {
            usage();
            setExitCode(1);
            return;
        }

        try {
            for (int i = 0; i < files.size(); i++) {
                GeneralizedFile f = files.get(i);
                if (f instanceof LocalFile) {
                    localFiles.add(f);
                } else {
                    String fileName = f.getFileName() + "-tmp";
                    String extension = FileManipulation.extension(fileName);
                    if (extension.isEmpty()) {
                        extension = "empty";
                    }

                    File output = File.createTempFile(fileName, "." + extension);
                    tasks.add(new CopyTask(f, new LocalFile(output.getAbsolutePath()), "", false));
                }
            }

            List<Long> timestamps = new ArrayList<Long>();
            copy(tasks);
            for (CopyTask task : tasks) {
                timestamps.add(task.dstDir.getDate());
            }

            /* Build command-line and execute editor */
            List<String> args = new ArrayList<String>();
            for (String c : rawEditor.split(" "))
                args.add(c);
            for (CopyTask task : tasks) {
                args.add(task.dstDir.getAbsolutePath());
            }
            for (GeneralizedFile file: localFiles) {
                args.add(file.givenName());
            }
            if (new File("/dev/tty").exists()) {
                String[] cmdArgs = new String[] {"sh", "-c", "'"
                                               + StringUtils.join(args, "' '")
                                               + "' > /dev/tty < /dev/tty"};
                DKUtils.execAndGetOutput(cmdArgs, null);
            } else {
                DKUtils.execAndGetOutput(args.toArray(new String[0]), null);
            }

            // Compute files that must be reuploaded
            List<CopyTask> backTasks = new ArrayList<CopyTask>();
            for (int i = 0; i < tasks.size(); i++) {
                long oldTS = timestamps.get(i);
                CopyTask task = tasks.get(i);
                long newTS = task.dstDir.getDate();
                if (oldTS != newTS) {
                    backTasks.add(new CopyTask(task.dstDir, task.src, "", false));
                }
            }
            copy(backTasks);

            // Delete temporary files
            for (CopyTask task : tasks) {
                task.dstDir.delete();
            }

        } catch (FileNotFoundException e) {
            error("File not found: " + e.getMessage(), 1);
        } catch (IOException e) {
            error(e.getMessage(), 1);
        } catch (InterruptedException e) {
            error(e.getMessage(), 2);
        }
    }

    // Protected
    @Override
    protected Options setOptions() {
        Options options = new Options();
        longOpt(options, "Override the EDITOR variable.", "editor", "e", "command");
        return options;
    }

    @Override
    protected String proto() {
        return "dctc edit PATH...";
    }

    // Private
    private void copy(List<CopyTask> tasks) throws IOException {
        SimpleCopyTaskRunnableFactory fact = new SimpleCopyTaskRunnableFactory(false, false, true);
        for (CopyTask task : tasks) {
            fact.build(task).work();
        }
    }
}
