package com.dataiku.dctc.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.file.FileBuilder.Protocol;
import com.dataiku.dip.utils.DKUFileUtils;

public class LocalFile extends AbstractGFile {
    // Constructors
    public LocalFile(File f) {
        this.f = f;
        fileName = f.getAbsolutePath();
    }
    public LocalFile(String path) {
        f = new File(path);
        fileName = path;
    }
    @Override // Should be override for no type lost.
    public List<LocalFile> createInstanceFor(List<String> paths) {
        if (paths != null) {
            List<LocalFile> res = new ArrayList<LocalFile>();
            for (int i = 0; i < paths.size(); ++i) {
                res.add(createInstanceFor(paths.get(i)));
            }
            return res;
        } else {
            return null;
        }
    }
    @Override
    public LocalFile createInstanceFor(String path) {
        return new LocalFile(path);
    }
    @Override
    public LocalFile createSubFile(String path, String separator) {
        return createInstanceFor(FileManipulation.concat(getAbsolutePath(), path, fileSeparator(), separator));
    }

    // Public
    @Override
    public boolean exists() {
        return f.exists();
    }
    @Override
    public boolean isFile() {
        return f.isFile() || (f.exists() && !this.isDirectory());
        // Is a file or a symlink.
    }
    @Override
    public boolean isDirectory() {
        return f.isDirectory();
    }
    @Override
    public String getAbsolutePath() {
        return f.getAbsolutePath();
    }
    @Override
    public String getAbsoluteAddress() {
        return getProtocol() + "://" + getAbsolutePath();
    }
    @Override
    public String getProtocol() {
        return Protocol.LOCAL.getCanonicalName();
    }
    @Override
    public String fileSeparator() {
        return System.getProperty("file.separator");
    }
    @Override
    public String forbiddenCharacter() {
        if (System.getProperty("os.name").startsWith("Windows")) {
            return "/\\?*:|\"<>";
        } else {
            return "/";
        }
    }
    @Override
    public String absolutePattern() {
        if (System.getProperty("os.name").startsWith("Windows")) {
            return GlobalConstants.WINDOWS_ROOT_PATH_PATTERN;
        }
        else {
            return "^/";
        }
    }
    protected List<String> list() throws IOException {
        if (l != null) {
            return l;
        }
        l = new ArrayList<String>();
        if (isDirectory()) {
            String[] files = f.list();
            if (files == null) {
                return l;
//                throw new IOException(givenName() + ": Access denied.");
            }
            for (String file: files) {
                l.add(FileManipulation.concat(fileName, file, fileSeparator()));
            }
        } else if (isFile()) {
            l.add(givenName());
        } else {
            throw new IOException(givenName() + ": File not found.");
        }
        return l;
    }
    @Override
    public List<LocalFile> glist() throws IOException {
        return createInstanceFor(list());
    }
    protected List<String> recursiveList() throws IOException {
        if (recurList != null) {
            return recurList;
        }
        recurList = new ArrayList<String>();
        if (isFile()) {
            recurList.add(givenName());
        } else if (isDirectory()) {
            recurList.add(givenName());
            for (String s: list()) {
                LocalFile f = new LocalFile(s);
                List<String> r = f.recursiveList();
                if (r == null) {
                    continue;
                }
                recurList.addAll(r);
            }
        }
        return recurList;
    }
    @Override
    public List<LocalFile> grecursiveList() throws IOException {
        return createInstanceFor(recursiveList());
    }
    @Override
    public String givenName() {
        return fileName;
    }
    @Override
    public String givenPath() {
        return fileName;
    }
    @Override
    public void mkdirs() throws IOException {
        File file = new File(pathToFile());
        DKUFileUtils.mkdirs(file);
    }
    @Override
    public void mkdir() throws IOException {
        File file = new File(getAbsolutePath());
        if (!file.mkdir()) {
            throw new IOException("unknown error creating " + file);
        }
    }
    @Override
    public void mkpath() throws IOException {
        File file = new File(getAbsolutePath()).getParentFile();
        DKUFileUtils.mkdirs(file);
    }
    @Override
    public InputStream inputStream() throws IOException {
        String path = f.getAbsolutePath();
        return new FileInputStream(path);
    }
    @Override
    public OutputStream outputStream() throws IOException {
        String path = f.getAbsolutePath();
        return new FileOutputStream(path);
    }
    @Override
    public boolean copy(GeneralizedFile input) throws IOException {
        return false;
    }
    @Override
    public boolean copy(InputStream contentStream, long size) throws IOException {
        return false;
    }
    @Override
    public boolean directCopy(GeneralizedFile ginput) throws IOException {
        // Possible, but hide the copy to the listener.
        return false;
    }
    @Override
    public boolean directMove(GeneralizedFile ginput) throws IOException {
        // Possible, but hide the copy to the listener.
        return false;
    }
    @Override
    public boolean delete() {
        if (isDirectory()) {
            try {
                FileUtils.deleteDirectory(f);
                return true;
            } catch (IOException e) {
                System.err.println("dctc LocalFile: " + e.getMessage());
                return false;
            }
        } else {
            return f.delete();
        }
    }
    @Override
    public boolean hasHash() {
        return true;
    }
    @Override
    public long maxFileSize() {
        return -1;
    }
    @Override
    public String getHash() throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            return org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
        } finally {
            IOUtils.closeQuietly(fis);
        }
    }
    @Override
    public String getHashAlgorithm() {
        return "MD5";
    }
    @Override
    public long getDate() {
        return f.lastModified();
    }
    @Override
    public long getSize() {
        return f.length();
    }
    @Override
    public boolean hasAcl() {
        return true;
    }
    @Override
    public Acl getAcl() {
        Acl acl = new Acl();
        if (isDirectory()) {
            acl.setFileType("d");
        }
        else {
            acl.setFileType("-");
        }
        acl.setRead("user", f.canRead());
        acl.setWrite("user", f.canWrite());
        acl.setExec("user", f.canExecute());
        acl.setRead("group", null);
        acl.setWrite("group", null);
        acl.setExec("group", null);
        acl.setRead("world", null);
        acl.setWrite("world", null);
        acl.setExec("world", null);

        return acl;
    }

    // Attributes
    private File f;
    private String fileName;
    private List<String> l;
    private List<String> recurList;
    @Override
    public InputStream getLastLines(long lineNumber) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public InputStream getLastBytes(long byteNumber) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public InputStream getRange(long begin, long length) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
}
