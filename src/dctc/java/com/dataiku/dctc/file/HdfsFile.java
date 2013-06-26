package com.dataiku.dctc.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RawLocalFileSystem;

import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.file.FileBuilder.Protocol;

public class HdfsFile extends AbstractGFile {
    // Constructors
    public HdfsFile(String path, String accountName, Configuration conf) {
        if (path.isEmpty()) {
            path = fileSeparator();
        }
        if (!path.startsWith(fileSeparator())) {
            path = fileSeparator() + path;
        }
        this.path = path;
        this.conf = conf;
        this.accountName = accountName;
        this.hdfsPath = new Path(this.path);
    }
    public HdfsFile(FileStatus status, FileSystem fileSystem) {
        this.status = status;
        this.path = status.getPath().getName();
        this.hdfsPath = new Path(path);
        this.fileSystem = fileSystem;
    }
    @Override // Should be override for no type lost.
    public List<HdfsFile> createInstanceFor(List<String> paths) {
        if (paths != null) {
            List<HdfsFile> res = new ArrayList<HdfsFile>();
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
    public HdfsFile createInstanceFor(String path) {
        return new HdfsFile(path, accountName, conf);
    }
    @Override
    public HdfsFile createSubFile(String path, String separator) {
        return createInstanceFor(FileManipulation.concat(this.path,
                                                         path,
                                                         fileSeparator(),
                                                         separator));
    }

    // Public
    @Override
    public boolean exists() throws IOException {
        initFileSystem();
        return fileSystem.exists(hdfsPath);
    }
    @Override
    public boolean isDirectory() throws IOException {
        try {
            initStatus();
        }
        catch (FileNotFoundException e) {
            return false;
        }
        return status.isDir();
    }
    @Override
    public boolean isFile() throws IOException {
        return fileSystem.isFile(hdfsPath);
    }
    @Override
    public String getAbsolutePath() {
        return path;
    }
    @Override
    public String getProtocol() {
        return Protocol.HDFS.getCanonicalName();
    }
    protected List<String> list() throws IOException {
        initStatus();
        FileStatus[] status = fileSystem.listStatus(hdfsPath);
        list = new ArrayList<String>();
        if (status == null) {
            return list;
        }
        else {
            for (FileStatus st: status) {
                list.add(FileManipulation.concat(path, st.getPath().getName(), fileSeparator()));
            }
            return list;
        }
    }
    @Override
    public List<HdfsFile> glist() throws IOException {
        return createInstanceFor(list());
    }
    protected List<String> recursiveList() throws IOException {
        if (recursiveList == null) {
            initStatus();
            recursiveList = new ArrayList<String>();
            Stack<FileStatus> task = new Stack<FileStatus>();
            task.add(status);
            while (!task.empty()) {
                FileStatus status = task.pop();
                Path path = status.getPath().getParent();
                recursiveList.add(FileManipulation.concat(path == null ? "" : path.toString().substring(7), status.getPath().getName(), fileSeparator()));
                if (status.isDir()) {
                    for (FileStatus st: fileSystem.listStatus(status.getPath())) {
                        task.add(st);
                    }
                }
            }
        }
        return recursiveList;
    }
    @Override
    public List<HdfsFile> grecursiveList() throws IOException {
        return createInstanceFor(recursiveList());
    }
    @Override
    public String givenName() {
        return getProtocol() + "://" + path;
    }
    @Override
    public String givenPath() {
        return path;
    }
    @Override
    public void mkdirs() throws IOException {
        initFileSystem();
        if (! fileSystem.mkdirs(new Path(path))) {
            throw new IOException("unknown error creating " + path);
        }
    }
    @Override
    public void mkdir() throws IOException {
        if (!createInstanceFor(pathToFile()).isDirectory()) {
            throw new IOException("Can't mkdir, parent folder "
                                  + pathToFile() + " does not exist or is not a directory");
        }
        mkdirs();
    }
    @Override
    public void mkpath() throws IOException {
        initFileSystem();
        if (!fileSystem.mkdirs(new Path(pathToFile()))) {
            throw new IOException("unknown error creating " + pathToFile());
        }
    }
    @Override
    public InputStream inputStream() throws IOException {
        initFileSystem();
        return fileSystem.open(hdfsPath);
    }
    @Override
    public OutputStream outputStream() throws IOException {
        initFileSystem();
        return fileSystem.create(hdfsPath);
    }
    @Override
    public boolean delete() throws IOException {
        return fileSystem.delete(hdfsPath, true);
    }
    @Override
    public boolean hasHash() {
        return false;
    }
    @Override
    public long maxFileSize() {
        return Long.MAX_VALUE;
    }
    @Override
    public String getHash() throws IOException {
        // HDFS does not support checksum by default
        return null;
    }
    @Override
    public String getHashAlgorithm() {
        return null; // HDFS does not support checksum by default
    }
    @Override
    public long getDate() throws IOException {
        initStatus();
        return status.getModificationTime();
    }
    @Override
    public long getSize() throws IOException{
        initStatus();
        // Hadoop do not compute the directory size. Set the default
        // size on local/ftp/ssh/
        if (isDirectory()) {
            return GlobalConstants.FOUR_KIO;
        }

        return status.getLen();
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

    // Local Methods
    // Private
    private void initFileSystem() throws IOException {
        if (fileSystem == null) {
            this.fileSystem = FileSystem.get(conf);
            if (this.fileSystem instanceof RawLocalFileSystem || this.fileSystem instanceof LocalFileSystem) {
                throw new IOException("HDFS initialization returned a LocalFileSystem. Maybe you need to configure your HDFS location ?");
            }
        }
    }
    private void initStatus() throws IOException {
        initFileSystem();
        this.status = this.fileSystem.getFileStatus(hdfsPath);
    }

    // Attributes
    private String path;
    private String accountName;
    private Path hdfsPath;
    private Configuration conf;
    private FileSystem fileSystem;
    private List<String> list;
    private List<String> recursiveList;
    private FileStatus status;
}
