package com.dataiku.dctc.copy;

import com.dataiku.dctc.file.GFile;

public class CopyTask {
    public CopyTask(GFile src, GFile dstDir,
                    String dstFileName, boolean deleteSrc) {
        this.src = src;
        this.dstDir = dstDir;
        this.dstFileName = dstFileName;
        this.deleteSrc = deleteSrc;
    }

    // The source file.
    public GFile src;
    // The destination directory.
    public GFile dstDir;
    // The destination file name. This attribute is a relative
    // path and could be empty. This argument is the source file
    // without the prefix given by the user.
    public String dstFileName;
    public boolean deleteSrc;
}
