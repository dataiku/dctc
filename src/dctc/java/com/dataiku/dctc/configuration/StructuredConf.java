package com.dataiku.dctc.configuration;

import static com.dataiku.dip.utils.PrettyString.eol;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import com.dataiku.dctc.file.FileBuilder;
import com.dataiku.dctc.file.FileBuilder.Protocol;

public class StructuredConf {
    public void parseSsh(File file) throws IOException {
        ssh.parse(file);
    }
    public void parseSsh(String path) throws IOException {
        ssh.parse(path);
    }

    public void parse(File file) throws IOException {
        conf.parse(file);
        alias();
        global();
        bank();
        Set<String> nonValidSection = conf.getNonValidSection();
        if (!nonValidSection.isEmpty()) {
            StringBuilder b = new StringBuilder();
            b.append("Invalid section detected:");
            for (String key: nonValidSection) {
                b.append(eol() + "dctc: error: Invalid section: " + key);
            }
            throw new IOException(b.toString());
        }
    }
    public void parse(String file) throws IOException {
        parse(new File(file));
    }
    public CredentialProviderBank getCredentialProviderBank() {
        return bank;
    }
    public Alias getAlias() {
        return alias;
    }
    public FileBuilder getFileBuilder() {
        if (builder == null) {
            builder = new FileBuilder(bank, ssh);
        }
        return builder;
    }

    // Private Methods
    private void alias() {
        alias.addAlias(conf.getOrCreateSection("alias"));
        conf.valid("alias");
    }
    private void bank() {
        for (Protocol protocol: Protocol.values()) {
            String proto = protocol.getCanonicalName();
            bank.addProtocolSettings(proto, conf.getOrCreateSection(proto));
            conf.valid(proto);
        }
    }
    private void global() {
        GlobalConf.setGlobalSettings(conf.getOrCreateSection("global"));
        conf.valid("global");
    }
    public Configuration getConf() {
        return conf;
    }
    public void setConf(Configuration conf) {
        this.conf = conf;
    }
    public StructuredConf withConf(Configuration conf) {
        this.conf = conf;
        return this;
    }

    // Attributes
    private Alias alias = new Alias();
    private CredentialProviderBank bank = new CredentialProviderBank();
    private Configuration conf = new Configuration();
    private FileBuilder builder;
    private SshConfig ssh = new SshConfig();
}
