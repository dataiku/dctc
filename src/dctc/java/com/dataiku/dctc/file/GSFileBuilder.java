package com.dataiku.dctc.file;

import com.dataiku.dctc.command.policy.YellPolicy;
import com.dataiku.dctc.file.FileBuilder.Protocol;
import com.dataiku.dip.utils.Params;

public class GSFileBuilder extends ProtocolFileBuilder {
    public Protocol getProtocol() { return Protocol.GS; }

    @Override
    public boolean validateAccountParams(String account, Params p) {
        return checkAllowedOnly(account, p
                                , new String[]{"mail"
                                               , "key_path"
                                               , "default_path"})
            || checkMandatory(account, p, "mail")
            || checkMandatory(account, p, "key_path");
    }

    @Override
    public synchronized GFile buildFile(String accountSettings
                                        , String rawPath
                                        , YellPolicy yell) {
        assert getBank() != null;
        Params p = getBank().getAccountParams(getProtocol().getCanonicalName()
                                              , accountSettings);
        if (validateAccountParams(accountSettings, p)) {
            throw invalidAccountSettings(accountSettings);
        }

        return new GSFile(p.getMandParam("mail"), p.getMandParam("key_path"),
                          translateDefaultPath(p, rawPath));
    }
    @Override
    public final String fileSeparator() {
        return "/";
    }
}
