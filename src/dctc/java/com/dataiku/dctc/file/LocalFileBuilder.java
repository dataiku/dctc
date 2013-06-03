package com.dataiku.dctc.file;

import com.dataiku.dctc.file.FileBuilder.Protocol;
import com.dataiku.dip.utils.Params;

public class LocalFileBuilder extends ProtocolFileBuilder {
    @Override
    public Protocol getProtocol() { return Protocol.FILE; }

    @Override
    public boolean validateAccountParams(String account, Params p, boolean fatal) {
        return false;
    }

    @Override
    public GeneralizedFile buildFile(String accountSettings, String rawPath) {
        return new LocalFile(rawPath);
    }
    @Override
    public final String fileSeparator() {
        return System.getProperty("file.separator");
    }
}
