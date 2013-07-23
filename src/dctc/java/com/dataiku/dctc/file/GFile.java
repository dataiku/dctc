package com.dataiku.dctc.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface GFile extends Comparable<GFile> {
    public GFile createInstanceFor(String path);
    public List<? extends GFile> createInstanceFor(List<String> paths);
    public GFile createSubFile(String path) throws IOException;
    public GFile createSubFile(String path, String fileSeparator) throws IOException;

    // MetaData and Naming.
    public boolean exists() throws IOException;
    public boolean isDirectory() throws IOException;
    public boolean isFile() throws IOException;
    public boolean isHidden() throws IOException;
    public boolean isTempFile() throws IOException;
    public boolean isEmpty() throws IOException;

    public String getFileName();
    public String getAbsolutePath();
    public String getAbsoluteAddress();
    // Traits
    public String forbiddenCharacter();
    public String getProtocol();
    public String fileSeparator();
    public String absolutePattern();
    public boolean canGetLastLines();
    public boolean canGetPartialFile();

    public InputStream getLastLines(long lineNumber) throws IOException;
    public InputStream getLastBytes(long byteNumber) throws IOException;
    public InputStream getRange(long begin, long length) throws IOException;
    public List<? extends GFile> glist() throws IOException;
    public List<? extends GFile> grecursiveList() throws IOException;

    public String givenName();
    public String givenPath();
    public String pathToFile() throws IOException; // For /foo/bar/baz return /foo/bar/.

    /**
     * Create the path as a folder. Fails if parents folders don't exist or if the path
     * already exists
     */
    public void mkdir() throws IOException;
    /**
     * Creates the path as a folder, including parent folders. Fails if one of the parent
     * paths or the path itself exists and is a file
     */
    public void mkdirs() throws IOException;
    /**
     * Create the path leading to this file, including intermediate folders.
     * IE, for /foo/bar/baz, creates /foo/bar
     */
    public abstract void mkpath() throws IOException;

    // Stream operation.
    public boolean hasOutputStream();
    public InputStream inputStream() throws IOException;
    public OutputStream outputStream() throws IOException;
    public boolean copy(GFile input) throws IOException;
    public boolean copy(InputStream contentStream, long size) throws IOException;
    public boolean directMove(GFile ginput) throws IOException;
    public boolean directCopy(GFile ginput) throws IOException;
    public boolean delete() throws IOException;

    public boolean hasHash();
    public boolean supportHashAlgorithm(String algorithm);
    public boolean hasDate();
    public boolean hasAcl();
    public Acl getAcl() throws IOException ;
    public boolean hasSize();
    public long maxFileSize();

    public String getHash() throws IOException;
    public String getHashAlgorithm();
    public long getDate() throws IOException;
    public void setDate(long date) throws IOException;
    public long getSize() throws IOException;
    public boolean allocate(long size);
}
