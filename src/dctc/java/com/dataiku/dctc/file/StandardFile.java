package com.dataiku.dctc.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class StandardFile implements GFile {
    @Override
    public int compareTo(GFile arg0) {
        if (arg0 instanceof StandardFile) {
            return 0;
        }
        return 1;
    }
    @Override
    public StandardFile createInstanceFor(String path) {
        if (!path.isEmpty()) {
            throw new Error("Invalid operation");
        }
        return this;
    }
    @Override
    public List<StandardFile> createInstanceFor(List<String> paths) {
        List<StandardFile> list = new ArrayList<StandardFile>();
        for (String path: paths) {
            list.add(createInstanceFor(path));
        }
        return list;
    }
    @Override
    public GFile createSubFile(String path) throws IOException {
        return createInstanceFor(path);
    }
    @Override
    public GFile createSubFile(String path, String fileSeparator) throws IOException {
        return createInstanceFor(path);
    }
    @Override
    public boolean exists() throws IOException {
        return true;
    }
    @Override
    public boolean isDirectory() throws IOException {
        return false;
    }
    @Override
    public boolean isFile() throws IOException {
        return true;
    }
    @Override
    public boolean isHidden() throws IOException {
        return false;
    }
    @Override
    public boolean isTempFile() throws IOException {
        return false;
    }
    @Override
    public boolean isEmpty() throws IOException {
        throw new IOException("Not a directory");
    }
    @Override
    public String getFileName() {
        return "-";
    }
    @Override
    public String getAbsolutePath() {
        return "-";
    }
    @Override
    public String getAbsoluteAddress() {
        return "-";
    }
    @Override
    public String forbiddenCharacter() {
        return "";
    }
    @Override
    public String getProtocol() {
        return "std";
    }
    @Override
    public String fileSeparator() {
        return "";
    }
    @Override
    public String absolutePattern() {
        return "";
    }
    @Override
    public boolean canGetLastLines() {
        return false;
    }
    @Override
    public boolean canGetPartialFile() {
        return false;
    }
    @Override
    public InputStream getLastLines(long lineNumber) throws IOException {
        return null;
    }
    @Override
    public InputStream getLastBytes(long byteNumber) throws IOException {
        return null;
    }
    @Override
    public InputStream getRange(long begin, long length) throws IOException {
        return null;
    }
    @Override
    public List<? extends GFile> glist() throws IOException {
        throw new IOException("Not a directory");
    }
    @Override
    public List<? extends GFile> grecursiveList() throws IOException {
        throw new IOException("Not a directory");
    }
    @Override
    public String givenName() {
        return "-";
    }
    @Override
    public String givenPath() {
        return "-";
    }
    @Override
    public String pathToFile() throws IOException {
        return "-";
    }
    @Override
    public void mkdir() throws IOException {
    }
    @Override
    public void mkdirs() throws IOException {
    }
    @Override
    public void mkpath() throws IOException {
    }
    @Override
    public boolean hasOutputStream() {
        return true;
    }
    @Override
    public InputStream inputStream() throws IOException {
        return System.in;
    }
    @Override
    public OutputStream outputStream() throws IOException {
        return System.out;
    }
    @Override
    public boolean copy(GFile input) throws IOException {
        return false;
    }
    @Override
    public boolean copy(InputStream contentStream, long size) throws IOException {
        return false;
    }
    @Override
    public boolean directMove(GFile ginput) throws IOException {
        return false;
    }
    @Override
    public boolean directCopy(GFile ginput) throws IOException {
        return false;
    }
    @Override
    public boolean delete() throws IOException {
        return false;
    }
    @Override
    public boolean hasHash() {
        return false;
    }
    @Override
    public boolean supportHashAlgorithm(String algorithm) {
        return false;
    }
    @Override
    public boolean hasDate() {
        return false;
    }
    @Override
    public boolean hasAcl() {
        return false;
    }
    @Override
    public Acl getAcl() throws IOException {
        return null;
    }
    @Override
    public boolean hasSize() {
        return false;
    }
    @Override
    public long maxFileSize() {
        return Long.MAX_VALUE;
    }
    @Override
    public String getHash() throws IOException {
        return null;
    }
    @Override
    public String getHashAlgorithm() {
        return null;
    }
    @Override
    public long getDate() throws IOException {
        return 0;
    }
    @Override
    public void setDate(long date) throws IOException {
    }
    @Override
    public long getSize() throws IOException {
        return -1;
    }
    @Override
    public boolean allocate(long size) {
        return true;
    }
}
