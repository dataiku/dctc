package com.dataiku.dctc.file;

import static com.dataiku.dip.utils.PrettyString.eol;
import static com.dataiku.dip.utils.PrettyString.pquoted;
import static com.dataiku.dip.utils.PrettyString.scat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.dataiku.dctc.AuthenticationFailedException;
import com.dataiku.dctc.DCTCLog;
import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.command.abs.Command.EndOfCommand;
import com.dataiku.dctc.configuration.SshConfig;
import com.dataiku.dctc.configuration.SshUserInfo;
import com.dataiku.dctc.exception.UserException;
import com.dataiku.dctc.file.FileBuilder.Protocol;
import com.dataiku.dip.utils.Params;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SshFile extends AbstractGFile {
    enum ConnectionType {
        IDENTITY
        , PASSWORD
        , PASSPHRASE
    }
    static class ConnectionData {
        String host;
        String username;
        String password;
        String sshKeyPath;
        String sshKeyPassphrase;
        int port;
        boolean skipHostKeyCheck;
        Session session;
        JSch jsch;
        String identity;
        int compressionLevel;

        String homePath;
    }

    private ConnectionData connData;

    private int parseInt(String integer, int low, int high) {
        int i = 0;
        try {
            i = Integer.parseInt(integer);
        }
        catch (NumberFormatException e) {
            DCTCLog.error("ssh file", scat(pquoted(integer), "is not a number."));
            throw new EndOfCommand();
        }
        if (low > i || i > high) {
            throw new UserException(scat("The number", integer, "is not between", low, "and", high));
        }

        return i;

    }
    public SshFile(SshConfig config, String host, String path, Params p) {
        if (path.isEmpty()) {
            this.path = ".";
        }
        else {
            this.path = path;
        }
        this.connData = new ConnectionData();
        this.connData.host = config.get(host, "HostName", host);
        this.connData.port = parseInt(config.get(host, "Port", "22"), 1, 65535);
        this.connData.username = config.get(host, "User", p.getMandParam("username"));
        this.connData.skipHostKeyCheck = p.getBoolParam("skip_host_key_check", false);
        this.connData.identity = config.get(host, "IdentityFile", p.getParam("identity"));
        this.connData.compressionLevel = parseInt(config.get(host, "CompressionLevel", "6"), 0, 9);
        this.connData.password = p.getParam("password");

        this.host = host;
    }

    public SshFile(String host, String username, String password, String path, int port,
                   boolean skipHostKeyCheck) {
        this.connData = new ConnectionData();
        this.host = host;
        this.connData.host = host;
        this.connData.port = port;
        this.connData.username = username;
        this.connData.password = password;
        if (path.isEmpty()) {
            this.path = ".";
        }
        else {
            this.path = path;
        }
    }
    public SshFile(String host, String username, String keyPath,
                   String keyPassphrase, String path, int port, boolean skipHostKeyCheck) {
        this.connData = new ConnectionData();
        this.connData.host = host;
        this.connData.port = port;
        this.connData.username = username;
        this.connData.sshKeyPath = keyPath;
        this.connData.sshKeyPassphrase = keyPassphrase;
        this.connData.skipHostKeyCheck = skipHostKeyCheck;
        this.path = path;
    }

    private SshFile(String path, SshFile copy) {
        this.connData = copy.connData;
        this.host = copy.host;
        this.path = path;
    }
    private SshFile(SshFile copy, String fileName, Acl acl, long size, long date) {
        this.connData = copy.connData;
        this.path = fileName;
        this.acl = acl;
        this.size = size;
        this.date = date;
        this.exists = true;
        if (acl.getFileType().equals("d")) {
            this.file = false;
            this.directory = true;
        }
        else {
            this.file = true;
            this.directory = false;
        }
    }

    @Override
    public SshFile createInstanceFor(String path) {
        return new SshFile(path, this);
    }
    @Override
    public List<SshFile> glist() throws IOException {
        if (list == null) {
            list = new ArrayList<SshFile>();
            String path = this.path;
            if (!path.endsWith(fileSeparator())) {
                path += "/";
            }
            path = path.replaceAll("#", "\\#");
            list(list, realpath + "; ls -a1 -- \"" + path + "\"| sed -e 's#^#" + path + "#'|" + format);
        }
        return list;
    }
    @Override
    public List<SshFile> grecursiveList() throws IOException {
        if (recursiveList == null) {
            recursiveList = new ArrayList<SshFile>();
            recursiveList.add(this);
            String path = this.path.replaceAll("'", "\\'");
            path = FileManipulation.trimEnd(path, "/");
            path = path.replaceAll("#", "\\#");
            list(recursiveList, realpath + "; find -- \"" + path + "\"| grep -v \"^" + path + "$\" | " + format);
        }
        return recursiveList;
    }
    @Override
    public SshFile createSubFile(String subpath, String fileSeparator) throws IOException {
        /* Absolutize the path if it's relative to the home */
        try {
            if (!path.startsWith("/")) {
                openSessionAndResolveHome();
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Unexpected error", e);
        }

        String childPathWithoutHost = FileManipulation.concat(path, subpath, fileSeparator(), fileSeparator);

        if (exists != null && exists == false) {
            return newNotFound(this, childPathWithoutHost);
        }

        if (recursiveList != null) {
            for (SshFile file : recursiveList) {
                if (file.getAbsolutePath().equals(connData.host + childPathWithoutHost)) {
                    return file;
                }
            }

            return newNotFound(this, childPathWithoutHost);
        }
        return new SshFile(FileManipulation.concat(path, subpath,
                                                   fileSeparator(), fileSeparator), this);
    }
    @Override
    public boolean exists() throws IOException {
        resolve();
        return exists;
    }
    @Override
    public boolean isDirectory() throws IOException {
        resolve();
        return exists && directory;
    }
    @Override
    public boolean isFile() throws IOException {
        resolve();
        return exists && file;
    }
    @Override
    public String getAbsolutePath() {
        try {
            if (!path.startsWith("/")) {
                openSessionAndResolveHome();
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Unexpected error", e);
        }
        if (!path.startsWith("/")) {
            System.err.println("Weird path encountered: " + path);
        }

        return path;
    }
    @Override
    public String getAbsoluteAddress() {
        return getProtocol() + "://" + this.connData.username
            + "@" + this.host + ":" + path;
    }
    @Override
    public String givenName() {
        /* We have to use the absolute path here because of the
         * weirdness of SSH when it comes to having a home folder
         */
        return getAbsoluteAddress();
    }
    @Override
    public String givenPath() {
        return path;
    }
    @Override
    public String getProtocol() {
        return Protocol.SSH.getCanonicalName();
    }
    @Override
    public void mkdir() throws IOException {
        if (!exec("mkdir  '" + path + "'; echo $?").equals("0\n")) {
            throw new IOException("Failed to mkdir " + path);
        }
    }

    @Override
    public void mkdirs() throws IOException {
        if (! exec("mkdir -p '" + path + "'; echo $?").equals("0\n")) {
            throw new IOException("Failed to mkdir " + path);
        }
    }
    @Override
    public void mkpath() throws IOException {
        String mkdirPath = FileManipulation.getPath(path, fileSeparator());
        if (! exec("mkdir -p '" + mkdirPath + "'; echo $?").equals("0\n")) {
            throw new IOException("Failed to mkdir " + path);
        }
    }
    @Override
    public InputStream getLastLines(long lineNumber) throws IOException {
        try {
            Channel channel = connect("tail -n" + lineNumber + " '" + path + "'");
            return channel.getInputStream();
        } catch (JSchException e) {
            throw new IOException("dctc sshfile: Failed to get inputstream for: " + path, e);
        }
    }
    @Override
    public InputStream getLastBytes(long byteNumber) throws IOException {
        try {
            Channel channel = connect("tail -c" + byteNumber + " '" + path + "'");
            return channel.getInputStream();
        }
        catch (JSchException e) {
            throw new IOException("dctc sshfile: Failed to get inputstream for: " + path, e);
        }
    }
    @Override
    public InputStream getRange(long begin, long nbByte) throws IOException {
        if (begin < 0) {
            nbByte += begin;
            begin = 0;
        }
        try {
            Channel channel = connect("tail -c+" + begin + " '" + path + "' | head -c" + nbByte);
            return channel.getInputStream();
        }
        catch (JSchException e) {
            throw new IOException("dctc sshfile: Failed to get inputstream for: " + path, e);
        }
    }

    @Override
    public InputStream inputStream() throws IOException {
        try {
            Channel channel = connect("cat '" + path + "'");
            return channel.getInputStream();
        }
        catch (JSchException e) {
            throw new IOException("dctc sshfile: Failed to get inputstream for: " + path, e);
        }
    }
    @Override
    public OutputStream outputStream() throws IOException {
        try {
            Channel channel = connData.session.openChannel("sftp");
            channel.connect();
            ChannelSftp channelSftp = (ChannelSftp)channel;
            try {
                return channelSftp.put(path, 0);
            }
            catch (SftpException e) {
                throw new IOException("dctc SshFile: Failed to get output stream for: " + path, e);

            }
        }
        catch (JSchException e) {
            throw new IOException("dctc SshFile: Failed to get output stream for: " + path, e);
        }
    }
    @Override
    public boolean delete() throws IOException {
        return exec("rm -rf  '" + path + "'; echo $?").equals("0\n");
    }
    @Override
    public boolean hasHash() {
        if (!hasHash.containsKey(connData.host)) {
            try {
                hasHash.put(connData.host,
                            exec("which md5sum | grep -v which > /dev/null; echo $?").equals("0\n"));
            }
            catch (IOException e) {
                return false;
            }
        }

        return hasHash.get(connData.host);
    }
    @Override
    public long maxFileSize() {
        return -1;
    }
    @Override
    public String getHash() throws IOException {
        return exec("md5sum " + path + " | cut -d' ' -f1");
    }
    @Override
    public long getDate() throws IOException {
        if (date == -1) {
            String date = exec("stat -c %Y " + path);
            if (!date.isEmpty()) {
                this.date = Long.parseLong(date.substring(0, date.length() - 1));
            }
        }
        return date;
    }
    @Override
    public long getSize() throws IOException {
        if (size == -1) {
            if (isDirectory()) {
                size = GlobalConstants.FOUR_KIO;
            }
            else {
                String size = exec("wc -c '" + path + "' | cut -d' ' -f1");
                if (!size.isEmpty()) {
                    this.size = Long.parseLong(size.substring(0, size.length() - 1));
                }
                else {
                    throw new IOException("File not found.");
                }
            }
        }

        return size;
    }
    @Override
    public String getHashAlgorithm() {
        return "MD5";
    }
    @Override
    public boolean allocate(long size) {
        this.size = size;
        return true;
    }
    public boolean hasAcl() {
        return true;
    }
    public Acl getAcl() {
        if (acl == null) {
            String[] split;
            try {
                split = exec("ls -dl '" + path + "' | sed -re 's/  */ /g'").split(" ");
            }
            catch (IOException e) {
                return acl;
            }

            acl = getAclFrom(split[0]);

            if (date == -1) {
                date = parseDate(split[5] + " " + split[6] + " " + split[7]);
            }
            if (size == -1) {
                size = Long.parseLong(split[4]);
            }
        }
        return acl;
    }

    @Override
    public boolean canGetLastLines() {
        return true;
    }
    @Override
    public boolean canGetPartialFile() {
        return true;
    }
    private void resolve() throws IOException {
        if (exists != null) {
            return;
        }
        if (!path.startsWith("/")) {
            try {
                openSessionAndResolveHome();
            }
            catch (JSchException e) {
                if (e.getCause() == null) {
                    String msg = e.getMessage();
                    if (msg.contains(":")) {
                        msg = msg.substring(msg.lastIndexOf(":") + 1).trim();
                    }
                    if (msg.contains("Too many authentication")) {
                        throw new AuthenticationFailedException(msg.substring(2).trim());
                    } else {
                        throw new IOException(scat("Unable to connect to", connData.host), e);
                    }
                }
                else if (e.getCause() instanceof UnknownHostException) {
                    throw (UnknownHostException)e.getCause();
                }
                else if (e.getCause() instanceof ConnectException) {
                    throw (ConnectException)e.getCause();
                }
                else {
                    throw new IOException(scat("Unable to connect to", connData.host), e);
                }
            }
        }

        String file = exec("file '" + path + "' | cut -d':' -f2 | tr -d ' ,\n'");
        if (file.equals("ERROR")) {
            exists = false;
        }
        else {
            exists = true;
            if (file.equals("directory") || file.equals("stickydirectory")) {
                directory = true;
                this.file = false;
            }
            else {
                this.file = true;
                directory = false;
            }
        }
    }
    private String exec(String command) throws IOException {
        try {
            Channel channel = connect(command);
            StringWriter writer = new StringWriter();
            IOUtils.copy(channel.getInputStream(), writer, "UTF-8");
            String str = writer.toString();
            disconnect(channel);
            int errorStatut = channel.getExitStatus();
            if (errorStatut != 0) {
                throw new IOException("Unknown error on: " + getAbsoluteAddress() + eol()
                                      + "receive: " + channel.getExitStatus() + eol() + str);
            }
            return str;
        }
        catch (JSchException e) {
            throw new IOException("Failed to execute SSH command on '" + path + "'", e);
        }
    }
    private Acl getAclFrom(String describ) {
        Acl acl = new Acl();

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

        return acl;
    }
    private SshFile parseAndBuild(String str) {
        Acl acl = getAclFrom(str.substring(0, str.indexOf(" ")));

        String absoluteFileName = str.substring(str.lastIndexOf("\"", str.length() - 2) + 1, str.length() - 1);
        str = str.substring(0, str.length() - absoluteFileName.length() - 3);
        String fileName = str.substring(str.lastIndexOf("\"", str.length() - 2) + 1, str.length() - 1);
        str = str.substring(0, str.length() - fileName.length() - 1);
        String[] split = str.split(" ");
        long size = Long.parseLong(split[2]);
        long date = Long.parseLong(split[1]) * 1000;

        return new SshFile(this, fileName, acl, size, date);
    }

    private long parseDate(String describ) {
        DateFormat dateFormat = new SimpleDateFormat("MMM d hh:mm");
        try {
            Date date = dateFormat.parse(describ);
            return date.getTime();
        }
        catch (ParseException e) {
            return -1;
        }
    }
    private void list(List<SshFile> l, String cmd) throws IOException {
        String[] split = exec(cmd).split("\n");
        boolean permissionDenied = true;

        for (String s: split) {
            permissionDenied = false;
            if (!s.isEmpty()) {
                SshFile son = parseAndBuild(s);
                if (!(son.getFileName().equals(".") || son.getFileName().equals(".."))) {
                    l.add(parseAndBuild(s));
                }
            }
        }
        if (permissionDenied) {
            throw new IOException("Permission denied: " + givenName());
        }
    }

    private static SshFile newNotFound(SshFile src, String path) {
        SshFile copy = new SshFile(path, src);
        copy.exists = false;
        copy.directory = false;
        copy.file = false;
        return copy;
    }

    private void openSessionAndResolveHome() throws IOException, JSchException {
        if (connData.session == null) {
            connData.jsch = new JSch();

            if (connData.sshKeyPath != null) {
                if (connData.sshKeyPassphrase != null){
                    connData.jsch.addIdentity(connData.sshKeyPath, connData.sshKeyPassphrase);
                } else {
                    connData.jsch.addIdentity(connData.sshKeyPath);
                }
            }
            else {
                if (connData.identity != null) {
                    File identity = new File(connData.identity);
                    if (identity.exists()) {
                        connData.jsch.addIdentity(identity.getAbsolutePath());
                    }
                }
                else {
                    File dsa = new File(System.getProperty("user.home") + "/.ssh/id_dsa");
                    if (dsa.exists()) {
                        connData.jsch.addIdentity(dsa.getAbsolutePath());
                    }
                    File rsa = new File(System.getProperty("user.home") + "/.ssh/id_rsa");
                    if (rsa.exists()) {
                        connData.jsch.addIdentity(rsa.getAbsolutePath());
                    }
                }
            }

            File knownHosts = new File(System.getProperties().get("user.home") + "/.ssh/known_hosts");
            if (knownHosts.exists()) {
                connData.jsch.setKnownHosts(knownHosts.getAbsolutePath());
            }

            JSch.setConfig("CompressionLevel", Integer.toString(connData.compressionLevel));

            //             DON'T REMOVE THAT. IT'S USEFUL FOR DEBUGGING
            //                    Logger l = new Logger() {
            //                        @Override
            //                        public void log(int arg0, String arg1) {
            //                            System.out.println(arg1);
            //                        }
            //
            //                        @Override
            //                        public boolean isEnabled(int arg0) {
            //                            return true;
            //                        }
            //                    };
            //                    connData.jsch.setLogger(l);

            connData.session = connData.jsch.getSession(connData.username,connData.host, connData.port);
            if (connData.skipHostKeyCheck) {
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                connData.session.setConfig(config);
            }
            if (connData.password != null) {
                connData.session.setUserInfo(new SshUserInfo(connData.password));
            }
            else {
                connData.session.setUserInfo(new SshUserInfo(connData.sshKeyPassphrase));
            }
            connData.session.connect();

            if (!path.startsWith("/")) {
                connData.homePath = exec("pwd").replace("\n", "");
                path = connData.homePath + "/" + path;
            }
        }
    }

    private Channel connect(String cmd)  throws JSchException, IOException {
        openSessionAndResolveHome();

        Channel channel = connData.session.openChannel("exec");
        ((ChannelExec) channel).setCommand(cmd);
        channel.connect();

        return channel;
    }
    private void disconnect(Channel channel) {
        channel.disconnect();
    }

    private String path;
    private Boolean exists;
    private Boolean file;
    private Boolean directory;
    private List<SshFile> recursiveList;
    private List<SshFile> list;
    private long size = -1;
    private long date = -1;
    private Acl acl;
    private static Map<String, Boolean> hasHash = new HashMap<String, Boolean>();
    private String host;

    private static String statOpt = "\\`if \"x$(uname)\" = \"xLinux\"; then echo f; "
        + "else echo c; fi\\`";
    private static String realpath = "dctc_real_path_() { path=$1; if [ -f $path ]; "
        + "then echo `pwd`/$path; else echo `cd -P -- \"${path:-.}\" && pwd`; fi; }";
    private static String deleteDotSlash = "if echo $line | grep \"^./\" > /dev/null; "
        + "then line=`echo $line | sed -e 's#^\\./##'`; fi";
    private static String format = "while read line; do " + deleteDotSlash + "; echo `stat -"
        + statOpt
        + " ' %A %Z %s %F \"%n\"' -- \"$line\"` \\\"`dctc_real_path_ \"$line\"`\\\"; done";
}
