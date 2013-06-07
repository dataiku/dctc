package com.dataiku.dctc.file;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.dataiku.dctc.file.FileBuilder.Protocol;
import com.dataiku.dip.utils.Params;

public class S3FileBuilder extends ProtocolFileBuilder {
    @Override
    public Protocol getProtocol() { return Protocol.S3; }


    private Map<String, AmazonS3> builtConnections = new HashMap<String, AmazonS3>();

    @Override
    public boolean validateAccountParams(String account, Params p) {
        return checkAllowedOnly(account, p, new String[]{"access_key", "secret_key", "default_path"})
            || checkMandatory(account, p, "access_key")
            ||checkMandatory(account, p, "secret_key");
    }

    @Override
    public synchronized GeneralizedFile buildFile(String accountSettings, String rawPath) {
        String accountName = bank.getResolvedAccountName(getProtocol().getCanonicalName(), accountSettings);
        Params p = bank.getAccountParams(getProtocol().getCanonicalName(), accountSettings);
        if (validateAccountParams(accountSettings, p)) {
            throw invalidAccountSettings(accountSettings);
        }

        AmazonS3 s3 = builtConnections.get(accountName);
        if (s3 == null) {
            ClientConfiguration conf = new ClientConfiguration();
            conf.setProtocol(com.amazonaws.Protocol.HTTP);
            s3 = new AmazonS3Client(new BasicAWSCredentials(p.getMandParam("access_key"),
                                                            p.getMandParam("secret_key")), conf);
            builtConnections.put(accountName, s3);
        }

        return new S3File(translateDefaultPath(p, rawPath), s3);
    }
    @Override
    public final String fileSeparator() {
        return "/";
    }
}
