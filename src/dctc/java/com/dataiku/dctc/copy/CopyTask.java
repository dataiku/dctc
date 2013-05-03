package com.dataiku.dctc.copy;

import com.dataiku.dctc.file.GeneralizedFile;

public class CopyTask {
    public CopyTask(GeneralizedFile src, GeneralizedFile dstDir, String dstFileName, boolean deleteSrc) {
        this.src = src;
        this.dstDir = dstDir;
        this.dstFileName = dstFileName;
        this.deleteSrc = deleteSrc;
    }

    // The source file.
    public GeneralizedFile src;
    // The destination directory.
    public GeneralizedFile dstDir;
    // The destination file name. This attribute is a relative
    // path and could be empty. This argument is the source file
    // without the prefix given by the user.
    public String dstFileName;
    public boolean deleteSrc;
}