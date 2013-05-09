package com.dataiku.dctc.file;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import com.google.common.collect.Sets;

import com.dataiku.dctc.configuration.CredentialProviderBank;
import com.dataiku.dctc.exception.UserException;
import com.dataiku.dctc.file.FileBuilder.Protocol;
import com.dataiku.dip.utils.Params;

public abstract class ProtocolFileBuilder {
    protected void  checkAllowedOnly(String account, Params p,
                                     String[] allowed) throws IllegalArgumentException{
        Set<String> allowedSet = Sets.newHashSet(allowed);
        for (String key : p.getAll().keySet()) {
            if (!allowedSet.contains(key)) {
                throw new UserException(String.format("For protocol %s and %s, parameter '%s' is not recognized." +
                                                      " Valid parameters are: %s", getProtocol().getCanonicalName(),
                                                      account == null ? "default account" : "account " + account, key,
                                                      StringUtils.join(allowed, ",")));
            }
        }
    }
    protected void checkMandatory(String account, Params p, String key) {
        if (!p.hasParam(key) || p.getParamOrEmpty(key).length() == 0) {
            throw new UserException(String.format("For protocol %s and %s, parameter '%s' is mandatory",
                                                  getProtocol().getCanonicalName(),
                                                  account == null ? "default account" : "account " + account, key));
        }
    }

    public void setBank(CredentialProviderBank bank) {
        this.bank = bank;
    }

    protected String translateDefaultPath(Params accountParams, String protocolData) {
        String defaultPath = accountParams.getParamOrEmpty("default_path");
        if (defaultPath.isEmpty()) {
            return protocolData;
        }
        if (FileManipulation.isAbsolute(protocolData, fileSeparator())) {
            return protocolData;
        }
        return FileManipulation.concat(defaultPath, protocolData, fileSeparator());
    }

    protected CredentialProviderBank bank;

    public abstract Protocol getProtocol();
    public abstract GeneralizedFile buildFile(String accountData, String protocolData);
    public abstract void validateAccountParams(String account, Params p);
    public abstract String fileSeparator();
}
