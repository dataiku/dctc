package com.dataiku.dctc.file;

import com.dataiku.dctc.file.FileBuilder.Protocol;
import com.dataiku.dip.utils.Params;

public class GSFileBuilder extends ProtocolFileBuilder {
    public Protocol getProtocol() { return Protocol.GS; }

    @Override
    public void validateAccountParams(String account, Params p) {
        checkAllowedOnly(account, p, new String[]{"mail", "key_path", "default_path"});
        checkMandatory(account, p, "mail");
        checkMandatory(account, p, "key_path");
    }

    @Override
    public synchronized GeneralizedFile buildFile(String accountData, String protocolData) {
        assert bank != null;
        Params p = bank.getAccountParams(getProtocol().getCanonicalName(), accountData);
        validateAccountParams(accountData, p);

        return new GSFile(p.getMandParam("mail"), p.getMandParam("key_path"),
                translateDefaultPath(p, protocolData));
    }
    @Override
    public final String fileSeparator() {
        return "/";
    }
}
