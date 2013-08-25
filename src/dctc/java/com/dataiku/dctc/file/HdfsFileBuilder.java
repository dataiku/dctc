package com.dataiku.dctc.file;

import org.apache.hadoop.conf.Configuration;

import com.dataiku.dctc.HadoopLoader;
import com.dataiku.dctc.command.policy.YellPolicy;
import com.dataiku.dctc.file.FileBuilder.Protocol;
import com.dataiku.dip.utils.Params;

public class HdfsFileBuilder extends ProtocolFileBuilder {

    @Override
    public Protocol getProtocol() {
        return Protocol.HDFS;
    }

    @Override
    public boolean validateAccountParams(String accountSettings, Params p) {
        return true;
    }

    @Override
    public GFile buildFile(String accountSettings, String path, YellPolicy yell) {
        HadoopLoader.addLibraries();
        try {
            return new HdfsFile(path, accountSettings);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to build HDFS file", e);
        }
    }
    @Override
    public final String fileSeparator() {
        return "/";
    }
}
