package com.dataiku.dctc.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.file.FileBuilder.Protocol;
import com.dataiku.dip.utils.Params;

public class FTPFile extends AbstractGFile {
    public FTPFile(String server, String username, String password, String path, short port) {
        this.server = server;
        this.username = username;
        this.password = password;
        this.port = port;
        this.path = FileManipulation.trimEnd(path, fileSeparator());
        if (this.path.length() == 0) {
            this.path = "/";
        }
    }
    public FTPFile(Params p, String path) {
        this(p.getMandParam("host"),
             p.getMandParam("username"),
             p.getMandParam("password"),
             path, p.getShortParam("port"));
    }

    @Override // Should be override for no type lost.
    public List<FTPFile> createInstanceFor(List<String> paths) {
        if (paths != null) {
            List<FTPFile> res = new ArrayList<FTPFile>();
            for (int i = 0; i < paths.size(); ++i) {
                res.add(createInstanceFor(paths.get(i)));
            }
            return res;
        } else {
            return null;
        }
    }
    @Override
    public FTPFile createInstanceFor(String path) {
        return new FTPFile(server, username, password, path, port);
    }
    @Override
    public FTPFile createSubFile(String path, String separator) {
        if (path.length() == 0) {
            return this;
        }
        return createInstanceFor(FileManipulation.concat(this.path, path, fileSeparator(), separator));
    }

    private void fastStat() throws IOException {
        // Init exists and file variable FAST.
        if (list != null) {
            return;
        }
        ftpInit();

        if (!cwd(path)) {
            if (!cwd(pathToFile())) {
                exists = false;
            }
            else {
                // Need to list, for opt could be done here.
                String fileName = getFileName();
                try {
                    for(String l: ftp.listNames()) {
                        if (fileName.equals(l)) {
                            exists = true;
                            file = true;
                            return;
                        }
                    }
                }
                catch (IOException e) {
                    exists = false;
                    throw e;
                }
            }
        }
        else {
            exists = true;
            file = false;
        }
    }

    // Public
    @Override
    public boolean exists() throws IOException {
        fastStat();
        return exists;
    }
    @Override
    public boolean isDirectory() throws IOException {
        fastStat();
        return exists && !file;
    }
    @Override
    public boolean isFile() throws IOException{
        fastStat();

        return exists && file;
    }
    @Override
    public String getAbsolutePath() {
        return path;
    }
    @Override
    public String getAbsoluteAddress() {
        return FileManipulation.concat(getProtocol() + "://" + username + ":*****@"
                                       + server, path, fileSeparator());
    }
    @Override
    public String getProtocol() {
        return Protocol.FTP.getCanonicalName();
    }
    protected List<String> list() throws IOException {
        if (list != null) {
            return list;
        }

        ftpInit();

        exists = false;
        file = false;
        if (cwd(path)) {
            list = new ArrayList<String>();
            exists = true;
            for (String file: ftp.listNames()) {
                String fileName = FileManipulation.concat(path, file, fileSeparator());
                if (!fileName.equals(path)) {
                    list.add(fileName);
                } else {
                    this.file = true;
                    break;
                }
            }
        }
        return list;
    }
    @Override
    public List<FTPFile> glist() throws IOException {
        return createInstanceFor(list());
    }
    protected List<String> recursiveList() throws IOException {
        if (recurList != null) {
            return recurList;
        }
        recurList = new ArrayList<String>(0);
        if (isFile()) {
            recurList.add(getAbsolutePath());
            return recurList;
        } else if (isDirectory()) {
            if (list() == null) {
                return recurList;
            }
            recurList.add(getAbsolutePath());
            for (String s: list()) {
                FTPFile f = createSubFile(FileManipulation.getSonPath(getAbsolutePath(), s, "/"), fileSeparator());
                recurList.add(f.getAbsolutePath());

                if (f.isDirectory()) {
                    List<String> rec = f.recursiveList();
                    if (rec == null) {
                        continue;
                    }
                    for (int i = 1; i < rec.size(); ++i) {
                        recurList.add(rec.get(i));
                    }
                }
            }
            return recurList;
        } else {
            return null; // Do not follow soft/hard link.
        }
    }
    @Override
    public List<FTPFile> grecursiveList() throws IOException {
        return createInstanceFor(recursiveList());
    }
    @Override
    public String givenName() {
        return "ftp://" + FileManipulation.concat(server, path, fileSeparator());
    }
    @Override
    public void mkdirs() throws IOException {
        ftpInit();
        cwd("/");
        if (!ftp.makeDirectory(getAbsolutePath())) {
            throw new IOException("Unknown error while creating " + pathToFile());
        }
    }
    @Override
    public void mkdir() throws IOException  {
        ftpInit();
        cwd("/");
        if (!ftp.makeDirectory(getAbsolutePath())) {
            throw new IOException("Unknown error while creating " + getAbsolutePath());
        }
    }
    @Override
    public void mkpath() throws IOException {
        ftpInit();
        cwd("/");
        if (!ftp.makeDirectory(pathToFile())) {
            throw new IOException("Unknown error while creating " + pathToFile());
        }
    }
    @Override
    public InputStream inputStream() throws IOException {
        try {
            ftpInit();
            ftp.cwd("/");
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            InputStream res = ftp.retrieveFileStream(path);;
            if (res == null) {
                throw new IllegalArgumentException();
            }

            return res;
        } catch (IllegalArgumentException e) {
            throw new IOException("No such file or directory " + getAbsoluteAddress());
        }
    }
    @Override
    public OutputStream outputStream() throws IOException {
        ftpInit();
        ftp.cwd("/");
        ftp.setFileType(FTP.BINARY_FILE_TYPE);

        return ftp.storeFileStream(path);
    }
    @Override
    public boolean directMove(GeneralizedFile ginput) throws IOException {
        if (!(ginput instanceof FTPFile)) {
            return false;
        }
        ftpInit();
        FTPFile input = (FTPFile) ginput;
        ftp.cwd("/");
        ftp.rename(input.path, path);
        return true;
    }
    @Override
    public boolean delete() {
        try {
            ftpInit();
            return isFile() ? ftp.deleteFile(path) : ftp.removeDirectory(path);
        } catch (IOException e) {
            return false;
        }
    }
    @Override
    public long maxFileSize() {
        return GlobalConstants.TWO_GIO;
        // return 2Gio.
    }
    @Override
    public long getDate() throws IOException {
        resolve();
        return date;
    }
    @Override
    public long getSize() throws IOException {
        resolve();
        return size;
    }
    // Local Method
    /// Method
    @Override
    public boolean hasAcl() {
        return true;
    }
    @Override
    public Acl getAcl() throws IOException {
        resolve();
        return acl;
    }

    /// Private
    private void createFtp() {
        ftp = new FTPClient();
    }
    private void connect() throws IOException {
        try {
            ftp.connect(server, port);
            if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new IOException("Fail Connect: " + e.getMessage());
        }
    }
    private void login() {
        try {
            ftp.enterLocalPassiveMode();
            if (!ftp.login(username, password)) {
                throw new IllegalArgumentException("Could not connect with account/password.");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not connect with account/password.");
        }
    }
    private void ftpInit() throws IOException {
        if (ftp != null) {
            return;
        }

        createFtp();
        connect();
        login();
    }
    private boolean cwd(String path) {
        try {
            ftp.cwd("/");
            if (path.equals(fileSeparator())) {
                return true;
            } else {
                ftp.cwd(path);
                return !ftp.printWorkingDirectory().equals("\"" + fileSeparator() + "\"");
            }
        } catch (IOException e) {
            return false;
        }
    }
    private void resolve() throws IOException {
        if (ftp != null && acl != null) {
            return;
        }
        ftpInit();
        ftp.cwd("/");
        ftp.cwd(pathToFile());
        String fileName = getFileName();
        for (org.apache.commons.net.ftp.FTPFile ftpFile: ftp.listFiles()) {
            if (ftpFile.getName().equals(fileName)) {
                parse(ftpFile);
            }
        }
    }
    private void parse(org.apache.commons.net.ftp.FTPFile ftpFile) {
        String raw = ftpFile.getRawListing();
        parseAcl(raw.substring(0, raw.indexOf(" ")));
        size = ftpFile.getSize();
        date = ftpFile.getTimestamp().getTime().getTime();

    }
    private void parseAcl(String describ) {
        acl = new Acl();
        if (describ.charAt(0) == 'd') {
            acl.setFileType("d");
        } else {
            acl.setFileType("-");
        }

        acl.setRead("user", describ.charAt(1) != '-');
        acl.setWrite("user", describ.charAt(2) != '-');
        acl.setExec("user", describ.charAt(3) != '-');

        acl.setRead("group", describ.charAt(4) != '-');
        acl.setWrite("group", describ.charAt(5) != '-');
        acl.setExec("group", describ.charAt(6) != '-');

        acl.setRead("world", describ.charAt(7) != '-');
        acl.setWrite("world", describ.charAt(8) != '-');
        acl.setExec("world", describ.charAt(9) != '-');
    }

    private boolean file;
    private boolean exists;
    private String server;
    private String username;
    private String password;
    private String path;
    private short port;
    private long date; // no 2038 bug.
    private long size = -1;
    private FTPClient ftp;
    private List<String> list;
    private List<String> recurList;
    private Acl acl;

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
    @Override
    public boolean hasHash() {
        return false;
    }
    @Override
    public String getHash() throws IOException {
        return getProtocol();
    }
}
