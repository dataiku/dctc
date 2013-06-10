package com.dataiku.dctc.copy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.archive.OutputArchiveEntry;
import com.dataiku.dctc.file.FileManipulation;
import com.dataiku.dctc.file.GeneralizedFile;

public class ArchiveCopyTaskRunnable extends CopyTaskRunnable {
    public ArchiveCopyTaskRunnable(CopyTask task, OutputArchiveEntry entry) {
        super(task.src);
        this.task = task;
        this.entry = entry;
    }
    public void work() throws IOException {
        if (task.src.isDirectory()) {
            return;
        }
        task.dstDir.mkpath();

        if (task.dstDir.hasOutputStream()) {
            byte[] b = new byte[GlobalConstants.ONE_MIO];

            GeneralizedFile input = task.src;
            entry.create(getEntryName());

            OutputStream out = entry.getContentStream();
            InputStream in = input.inputStream();

            int s;
            while (true) {
                s = in.read(b);
                inc(s);
                if (s == -1) {
                    break;
                }
                out.write(b, 0, s);
            }
            in.close();
            entry.closeEntry();

        } else {
            System.err.println("FIXME");
        }
    }

    public String print()  {
        return task.src.givenName() + " ->> " + task.dstDir.givenName()
                + " (as "
                + getEntryName() + ")";
    }

    private String getEntryName() {
        if (task.dstFileName.isEmpty()) {
            return task.src.getFileName();
        } else {
            return FileManipulation.makeRelative(task.dstFileName,
                    task.src.absolutePattern());
        }
    }

    private CopyTask task;
    private OutputArchiveEntry entry;
}
