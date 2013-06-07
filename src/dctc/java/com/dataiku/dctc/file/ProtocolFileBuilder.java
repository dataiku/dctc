package com.dataiku.dctc.file;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import com.google.common.collect.Sets;

import com.dataiku.dctc.configuration.CredentialProviderBank;
import com.dataiku.dctc.exception.UserException;
import com.dataiku.dctc.file.FileBuilder.Protocol;
import com.dataiku.dip.utils.Params;

public abstract class ProtocolFileBuilder {
    protected boolean checkAllowedOnly(String account, Params p,
                                     String[] allowed,
                                     boolean fatal) throws IllegalArgumentException {
        boolean failed = false;
        Set<String> allowedSet = Sets.newHashSet(allowed);
        for (String key : p.getAll().keySet()) {
            if (!allowedSet.contains(key)) {
                String msg = String.format("For protocol %s and %s, parameter '%s' is not recognized." +
                                           " Valid parameters are: %s", getProtocol().getCanonicalName(),
                                           account == null ? "default account" : "account " + account, key,
                                           StringUtils.join(allowed, ", "));
                if (fatal) {
                    throw new UserException(msg);
                }
                else {
                    System.err.println(msg);
                    failed = true;
                }
            }
        }
        return failed;
    }
    protected boolean checkMandatory(String account, Params p, String key, boolean fatal) {
        if (!p.hasParam(key) || p.getParamOrEmpty(key).isEmpty()) {
            String msg = String.format("For protocol %s and %s, parameter '%s' is mandatory",
                                       getProtocol().getCanonicalName(),
                                       account == null ? "default account" : "account " + account, key);
            if (fatal) {
                throw new UserException(msg);
            }
            else {
                System.err.println(msg);
                return true;
            }
        }
        return false;
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

    // Abstract
    public abstract Protocol getProtocol();
    public abstract GeneralizedFile buildFile(String accountData, String protocolData);
    public abstract boolean validateAccountParams(String account, Params p, boolean fatal);
    public abstract String fileSeparator();

    // Attributes
    protected CredentialProviderBank bank;
}
