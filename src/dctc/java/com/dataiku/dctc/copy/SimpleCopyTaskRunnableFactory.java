package com.dataiku.dctc.copy;

import java.io.IOException;

import com.dataiku.dctc.archive.ArchiveFactory;
import com.dataiku.dctc.archive.OutputArchiveIterable;
import com.dataiku.dctc.archive.OutputArchiveIterator;
import com.dataiku.dctc.file.GFile;

// Direct move with archiving/unarchiving management.
public class SimpleCopyTaskRunnableFactory implements CopyTaskRunnableFactory {
    public SimpleCopyTaskRunnableFactory(boolean unarchive, boolean archive, boolean preserveDate) {
        this.unarchive = unarchive;
        this.archive = archive;
        this.preserveDate = preserveDate;
    }
    @Override
    public CopyTaskRunnable build(CopyTask task) throws IOException {
        if (unarchive) {
            String fileName = task.src.givenName();
            if (fileName.endsWith(".zip")
                || fileName.endsWith(".tar")
                || fileName.endsWith(".tar.bz2")
                || fileName.endsWith(".tar.gz")) {
                if (task.dstDir.givenName().endsWith(task.dstDir.fileSeparator())) {
                    return new UnarchiveCopyTask(task.src, task.dstDir);

                } else {
                    GFile dst = task.dstDir;
                    return new UnarchiveCopyTask(task.src, dst);
                }
            }
        }
        if (archive) {
            if (archiveIterable == null) {
                if (task.dstDir.exists() && task.dstDir.isDirectory()) {
                    archiveIterable = ArchiveFactory.buildOutput(task
                                                                 .dstDir
                                                                 .createSubFile(task.dstDir.getFileName()));
                }
                else {
                    archiveIterable = ArchiveFactory.buildOutput(task.dstDir);
                }

                ite = archiveIterable.iterator();
            }
            return new ArchiveCopyTaskRunnable(task, ite.next());
        }
        else {
            return new DirectCopyTaskRunnable(task.src
                                              , task.dstDir.createSubFile(task.dstFileName
                                                                          , task.src.fileSeparator())
                                              , task.deleteSrc
                                              , preserveDate);
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
    private boolean preserveDate;
    private OutputArchiveIterable archiveIterable = null;
    private OutputArchiveIterator ite;
}
