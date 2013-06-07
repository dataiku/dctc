package com.dataiku.dctc.file;

import com.dataiku.dctc.file.FileBuilder.Protocol;
import com.dataiku.dip.utils.Params;

public class HttpFileBuilder extends ProtocolFileBuilder {

    @Override
    public Protocol getProtocol() {
        return Protocol.HTTP;
    }

    @Override
    public boolean validateAccountParams(String accountSettings, Params p) {
        return checkAllowedOnly(accountSettings, p, new String[]{});
    }

    @Override
    public HttpFile buildFile(String accountSettings, String rawPath) {
        return new HttpFile(rawPath);
    }
    @Override
    public final String fileSeparator() {
        return "/";
    }
}
