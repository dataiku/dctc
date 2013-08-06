package com.dataiku.dip.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
    
    public static void delete(File f) throws IOException {
        if (f.exists()) {
            boolean ret = f.delete();
            if (ret == false) {
                throw new IOException("Failed to delete " + f);
            }
        }
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
    public static String fileToString(File path) throws FileNotFoundException, IOException {
        try {
            return fileToString(path, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new Error("Never appends");
        }
    }
    public static String fileToString(File path,
                                      String encoding) throws UnsupportedEncodingException,
                                                              FileNotFoundException,
                                                              IOException {
        return streamToString(StreamUtils.readFile(path, encoding));
    }
    public static String streamToString(BufferedReader in) throws IOException{
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }

        return sb.toString();
    }
}
