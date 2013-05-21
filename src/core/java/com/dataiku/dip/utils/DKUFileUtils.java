package com.dataiku.dip.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class DKUFileUtils {
    public static void mkdirs(File folder) throws IOException {
        if (folder.exists()) {
            if (folder.isDirectory()) return;
            else {
                throw new IOException("Can't create " + folder.getAbsolutePath()+ ": is a file");
            }
        }

        FileUtils.forceMkdir(folder);
    }
    public static void mkdirsParent(File file) throws IOException {
        mkdirs(file.getParentFile());
    }
    
    public static String readFileToStringUTF8(File file) throws IOException {
        return FileUtils.readFileToString(file, "utf8");
    }
    public static void writeFileUTF8(File file, String content) throws IOException {
        FileUtils.write(file, content, "utf8");
    }
}
