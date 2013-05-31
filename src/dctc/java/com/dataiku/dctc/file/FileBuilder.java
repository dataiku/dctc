package com.dataiku.dctc.file;

import com.dataiku.dctc.configuration.CredentialProviderBank;
import com.dataiku.dctc.configuration.SshConfig;
import com.dataiku.dctc.exception.UserException;

public class FileBuilder {
    public enum Protocol {
        FTP("ftp", new FTPFileBuilder()),
        FILE("file", new LocalFileBuilder()),
        GS("gs", new GSFileBuilder()),
        HDFS("hdfs", new HdfsFileBuilder()),
        HTTP("http", new HttpFileBuilder()),
        S3("s3", new S3FileBuilder()),
        SSH("ssh", new SshFileBuilder());

        Protocol(String canonicalName, ProtocolFileBuilder builder) {
            this.canonicalName = canonicalName;
            this.builder = builder;
        }

        public String getCanonicalName() {
            return canonicalName;
        }

        private String canonicalName;
        private ProtocolFileBuilder builder;

        public static Protocol forName(String protocol) {
            protocol = protocol.toLowerCase();
            for (Protocol proto: Protocol.values()) {
                if (protocol.equals(proto.getCanonicalName())) {
                    return proto;
                }
            }
            if (protocol.equals("local")) {
                return FILE;
            }
            throw new UserException("Unknown protocol: " + protocol);
        }
    }

    public FileBuilder(CredentialProviderBank bank, SshConfig sshConfig) {
        this.bank = bank;
        bank = new CredentialProviderBank();
        this.sshConfig = sshConfig;
    }

    public GeneralizedFile buildFile(String uri) {
        int protocolSeparator = uri.indexOf("://");
        if (protocolSeparator == -1) {
            return Protocol.FILE.builder.buildFile(null, uri);
        }

        Protocol protocol = Protocol.forName(uri.substring(0, protocolSeparator));
        String path  = uri.substring(protocolSeparator + 3, uri.length());

        return buildFile(protocol, path);
    }
    public GeneralizedFile buildFile(String proto, String path) {
        return buildFile(Protocol.forName(proto), path);
    }
    public GeneralizedFile buildFile(Protocol proto, String path) {
        String account = null;
        int atIndex = path.indexOf("@");
        if (atIndex > 0) {
            account = path.substring(0, atIndex);
            path = path.substring(atIndex + 1);
        }
        proto.builder.setBank(bank);
        if (proto == Protocol.SSH) {
            ((SshFileBuilder) proto.builder).setSshConfig(sshConfig);
        }
        return proto.builder.buildFile(account, path);

    }

    public GeneralizedFile[] buildFile(String[] paths) {
        GeneralizedFile[] res = new GeneralizedFile[paths.length];
        for (int i = 0; i < paths.length; ++i) {
            res[i] = buildFile(paths[i]);
        }
        return res;
    }

    private CredentialProviderBank bank;
    private SshConfig sshConfig;
}
