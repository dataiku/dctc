package com.dataiku.dctc.file;

import com.dataiku.dctc.file.FileBuilder.Protocol;
import com.dataiku.dip.utils.Params;

public class LocalFileBuilder extends ProtocolFileBuilder {
    @Override
    public Protocol getProtocol() { return Protocol.LOCAL; }

    @Override
    public void validateAccountParams(String account, Params p) {
    }

    @Override
    public GeneralizedFile buildFile(String accountData, String protocolData) {
        return new LocalFile(protocolData);
    }
    @Override
    public final String fileSeparator() {
        return System.getProperty("file.separator");
    }
}
