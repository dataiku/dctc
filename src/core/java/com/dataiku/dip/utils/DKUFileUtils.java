package com.dataiku.dip.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    
    public static void writeFileUTF8(File file, String content, boolean mkdirs) throws IOException {
        if (mkdirs) {
            mkdirsParent(file);
        }
        FileUtils.write(file, content, "utf8");
    } 
    
    public static List<File> recursiveListFiles(File root) throws IOException {
        if (!root.isDirectory()) {
            throw new IOException("Root " + root + " is not a directory");
        }
        List<File>  ret = new ArrayList<File>();
        listRec(root, ret);
        return ret;
    }
    
    private static void listRec(File folder, List<File> ret) {
        for (File f : folder.listFiles()) {
            if (f.isDirectory()) listRec(f, ret);
            else ret.add(f);
        }
    }
}
