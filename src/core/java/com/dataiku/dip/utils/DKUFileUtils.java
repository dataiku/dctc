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

    /**
     * Return the list of the files that are, (at any depth)
     * under this directory
     *
     * The resulting list is a list of files sorted "depth-first."
     */
    public static List<File> recursiveListFiles(File root) throws IOException {
        return recursiveListFiles(root, FileFilter.FILES);
    }

    public static void removeDirectoryRecursive(File root) throws IOException {
        List<File> files = recursiveListFiles(root, FileFilter.FILES_AND_DIRECTORY);
        for (File file: files ){
            DKUFileUtils.delete(file);
        }
    }

    public static List<File> recursiveListFiles(File root, FileFilter fileFilter) throws IOException {
        if (!root.isDirectory()) {
            throw new IOException("Root " + root + " is not a directory");
        }
        List<File>  ret = new ArrayList<File>();
        listRec(root, ret, fileFilter);
        return ret;
    }

    public static enum FileFilter {
        FILES {
            @Override
            boolean accept(File f) {
                return !f.isDirectory();
            }
        },
        FILES_AND_DIRECTORY {
            @Override
            boolean accept(File f) {
                return true;
            }
        };
        abstract boolean accept(File f);
    }

    private static void listRec(File f, List<File> ret, FileFilter fileFilter) {
        if (f.isDirectory()) {
            for (File child : f.listFiles()) {
                listRec(child, ret, fileFilter);
            }
        }
        if (fileFilter.accept(f)) {
            ret.add(f);
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

    public static int highestFileNumber(File root, String prefix) {
        int max = -1;
        if (!root.isDirectory()) return max;
        for (File f : root.listFiles()) {
            if (f.getName().startsWith(prefix)) {
                try {
                    int cur = Integer.parseInt(f.getName().replace(prefix, ""));
                    max = Math.max(cur, max);
                } catch (Exception e) {}
            }
        }
        return max;
    }
    
    public static int nextFileNumber(File root, String prefix) {
        int backupVersion = -1;
        int lastSavedVersion = highestFileNumber(root, prefix);
        if (lastSavedVersion == -1) backupVersion = 1;
        else backupVersion = lastSavedVersion + 1;
        return backupVersion;
    }
}
