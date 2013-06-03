package com.dataiku.dctc.file;

import com.dataiku.dctc.file.FileBuilder.Protocol;
import com.dataiku.dip.utils.Params;

public class GSFileBuilder extends ProtocolFileBuilder {
    public Protocol getProtocol() { return Protocol.GS; }

    @Override
    public boolean validateAccountParams(String account, Params p, boolean fatal) {
        return checkAllowedOnly(account, p, new String[]{"mail", "key_path", "default_path"}, fatal)
            || checkMandatory(account, p, "mail", fatal)
            || checkMandatory(account, p, "key_path", fatal);
    }

    @Override
    public synchronized GeneralizedFile buildFile(String accountSettings, String rawPath) {
        assert bank != null;
        Params p = bank.getAccountParams(getProtocol().getCanonicalName(), accountSettings);
        validateAccountParams(accountSettings, p, true);

        return new GSFile(p.getMandParam("mail"), p.getMandParam("key_path"),
                translateDefaultPath(p, rawPath));
    }
    @Override
    public final String fileSeparator() {
        return "/";
    }
}
