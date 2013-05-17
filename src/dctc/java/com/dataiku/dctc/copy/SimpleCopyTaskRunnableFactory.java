package com.dataiku.dctc.copy;

import java.io.IOException;

import com.dataiku.dctc.archive.ArchiveFactory;
import com.dataiku.dctc.archive.OutputArchiveIterable;
import com.dataiku.dctc.archive.OutputArchiveIterator;
import com.dataiku.dctc.file.GeneralizedFile;

// Direct move with archiving/unarchiving management.
public class SimpleCopyTaskRunnableFactory implements CopyTaskRunnableFactory {
    public SimpleCopyTaskRunnableFactory(boolean unarchive, boolean archive) {
        this.unarchive = unarchive;
        this.archive = archive;
    }
    @Override
    public CopyTaskRunnable build(CopyTask task) throws IOException {
        if (unarchive && task.src.givenName().endsWith(".zip")) {
            if (task.dstDir.givenName().endsWith(task.dstDir.fileSeparator())) {
                return new UnarchiveCopyTask(task.src, task.dstDir);
            } else {
                GeneralizedFile dst = task.dstDir.createSubFile("", task.dstDir.fileSeparator());
                return new UnarchiveCopyTask(task.src, dst);
            }
        } else if (archive) {
            if (archiveIterable == null) {
                try {
                    archiveIterable = ArchiveFactory.buildOutput(task.dstDir);
                } catch (IOException e) {
                    System.err.println("dctc DirectCopyFactory: " + e.getMessage());
                }
                ite = archiveIterable.iterator();
            }
            return new ArchiveCopyTaskRunnable(task, ite.next());
        } else {
            return new DirectCopyTaskRunnable(task.src,
                                              task.dstDir.createSubFile(task.dstFileName,
                                                                        task.src.fileSeparator()),
                                              task.deleteSrc,
                                              preserveDate);
        }
    }

    @Override
    public void done() throws IOException {
        if (ite != null) {
            ite.close();
        }
    }

    private boolean archive;
    private boolean unarchive;
    private OutputArchiveIterable archiveIterable = null;
    private OutputArchiveIterator ite;
}
