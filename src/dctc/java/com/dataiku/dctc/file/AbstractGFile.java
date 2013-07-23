package com.dataiku.dctc.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

public abstract class AbstractGFile implements GFile {
    @Override
    public final int compareTo(GFile arg) {
        String l = tr(getAbsoluteAddress());
        String r = tr(arg.getAbsoluteAddress());

        return l.compareTo(r);
    }
    private String tr(String f) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < f.length(); ++i) {
            char c = f.charAt(i);

            sb.append(c);
        }

        return sb.toString();
    }
    @Override // Should be override for no type lost.
    public List<? extends GFile> createInstanceFor(List<String> paths) {
        if (paths != null) {
            List<GFile> res = new ArrayList<GFile>();
            for (int i = 0; i < paths.size(); ++i) {
                res.add(createInstanceFor(paths.get(i)));
            }
            return res;
        }
        else {
            return null;
        }
    }
    @Override
    public final GFile createSubFile(String path) throws IOException {
        return createSubFile(path, fileSeparator());
    }
    @Override
    public final boolean isHidden() throws IOException {
        return FileManipulation.isHidden(getAbsolutePath(), fileSeparator());
    }
    @Override
    public final boolean isTempFile() throws IOException {
        return FileManipulation.isTempFile(getAbsolutePath(), fileSeparator());
    }
    @Override
    public boolean isEmpty() throws IOException {
        if (!isDirectory()) {
            throw new IOException("Not a directory");
        }
        return glist() == null || glist().size() == 0;
    }
    @Override
    public final String getFileName() {
        return FileManipulation.getFileName(givenPath(), fileSeparator());
    }
    @Override
    public String getAbsoluteAddress() {
        return getProtocol() + "://" + getAbsolutePath();
    }
    @Override // Could be override.
    public String fileSeparator() {
        return "/";
    }
    @Override // Could be override.
    public String absolutePattern() {
        return "^/";
    }
    @Override
    public final String pathToFile() throws IOException {
        return FileManipulation.getPath(getAbsolutePath(), fileSeparator());
    }

    @Override
    public boolean canGetLastLines() {
        return false;
    }
    @Override
    public boolean canGetPartialFile() {
        return false;
    }
    @Override // Could be override.
    public boolean hasOutputStream() {
        return true;
    }
    @Override // Could be override.
    public boolean copy(GFile input) throws IOException {
        if (input.hasSize()) {
            copy(input.inputStream(), input.getSize());
        } else {
            IOUtils.copyLarge(input.inputStream(), outputStream());
        }
        return true;
    }
    @Override
    public boolean copy(InputStream contentStream,
                        long size) throws IOException {
        IOUtils.copyLarge(contentStream, outputStream());
        return true;
    }
    @Override // Should be override if needed.
    public boolean directMove(GFile ginput) throws IOException {
        return false;
    }
    @Override // Should be override if needed.
    public boolean directCopy(GFile ginput) throws IOException {
        return false;
    }
    @Override
    public final boolean supportHashAlgorithm(String algorithm) {
        return getHashAlgorithm().equals(algorithm);
    }
    @Override
    public boolean hasDate() {
        return true;
    }
    @Override
    public boolean hasAcl() {
        return false;
    }
    public Acl getAcl() throws IOException {
        throw new IllegalArgumentException("Acl is not implemented for " + getProtocol());
    }
    @Override
    public boolean hasSize() {
        return true;
    }
    @Override // Should be override if hash is supported
    public String getHashAlgorithm() {
        assert !hasHash()
            : "!hasHash()";
        return getProtocol();
    }
    @Override
    public String forbiddenCharacter() {
        return "";
    }
    @Override
    public void setDate(long date) throws IOException {
    }
    @Override // Could be override.
    public boolean allocate(long size) {
        if (maxFileSize() == -1 || size < maxFileSize()) {
            // Don't need to allocate
            return true;
        }
        return false;
    }

    // Protected methods
    protected String clean(String path) {
        if (FileManipulation.isAbsolute(path, absolutePattern())) {
            return getProtocol() + ":/" + path;
        }
        else {
            return getProtocol() + "://" + path;
        }
    }

    /* Create the path leading to this file, including intermediate folders */
    public abstract void mkpath() throws IOException;

    // Abstract Method.
    public abstract GFile createSubFile(String path, String fileSeparator) throws IOException;
    public abstract boolean exists() throws IOException;
    public abstract boolean isDirectory() throws IOException;
    public abstract boolean isFile() throws IOException;
    public abstract String getAbsolutePath();
    public abstract String givenName();
    public abstract String givenPath();
    public abstract String getProtocol();
    public abstract void mkdirs() throws IOException;
    public abstract void mkdir() throws IOException;
    public abstract InputStream inputStream() throws IOException;
    public abstract OutputStream outputStream() throws IOException;
    public abstract boolean delete() throws IOException;
    public abstract boolean hasHash();
    public abstract long maxFileSize();
    public abstract String getHash() throws IOException;

    public abstract long getDate() throws IOException;
    public abstract long getSize() throws IOException;

}
