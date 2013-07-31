package com.dataiku.dctc.configuration;

import static com.dataiku.dip.utils.PrettyString.pquoted;
import static com.dataiku.dip.utils.PrettyString.scat;

import java.util.HashMap;
import java.util.Map;

import com.dataiku.dctc.exception.UserException;
import com.dataiku.dctc.file.PathManip;
import com.dataiku.dip.utils.Params;

public class CredentialProviderBank {
    public CredentialProviderBank() {
    }
    private void addCredentialParam(String protocol
                                    , String account
                                    , String key
                                    , String value) {
        ProtocolCredentials pcre = protocolCredentials.get(protocol);
        if (pcre == null) {
            pcre = new ProtocolCredentials();
            protocolCredentials.put(protocol, pcre);
        }
        Params accountCred = pcre.get(account);
        if (accountCred == null) {
            accountCred = new Params();
            pcre.put(account, accountCred);
        }
        accountCred.add(key, value);
    }
    public void addProtocolSettings(String protocol
                                    , Map<String, String> settings) {
        String firstAccount = null;
        for (Map.Entry<String, String> setting: settings.entrySet()) {
            String key = setting.getKey();
            String value = setting.getValue();

            if (key.contains(".")) {
                String[/*user/option*/] opt = PathManip.split(key, ".", 2);
                if (firstAccount == null) {
                    firstAccount = opt[0];
                }
                addCredentialParam(protocol, opt[0], opt[1], value);
            }
            else if (key.equals("default")) {
                protocolToDefaultAccount.put(protocol, value);
            }
            else {
                throw new UserException(scat("Unexpected parameter"
                                             , pquoted(key)
                                             , "in the protocol section"
                                             , pquoted(protocol)
                                             + ", "
                                             , "expected either `default' or"
                                             + " `account.param' key."));
            }
        }
        if (protocolToDefaultAccount.containsKey(protocol)) {
            String defaultAccount = protocolToDefaultAccount.get(protocol);

            if (getAccountParamsIfExists(protocol, defaultAccount) == null) {
                throw new UserException(scat("Invalid default account"
                                             , pquoted(defaultAccount)
                                             , "for protocol"
                                             , pquoted(protocol)));
            }
        }
        else {
            protocolToDefaultAccount.put(protocol, firstAccount);
        }
    }
    public ProtocolCredentials getProtocolCredentials(String protocol) {
        return protocolCredentials.get(protocol);
    }
    public Params getAccountParams(String protocol, String account) {
        assert(protocol != null);

        ProtocolCredentials creds = protocolCredentials.get(protocol);
        checkCredentials(creds, protocol);

        if (account == null) {
            account = protocolToDefaultAccount.get(protocol);
        }

        Params p = creds.get(account);
        checkCredentials(p, protocol);

        return p;
    }
    public String getResolvedAccountName(String protocol, String account) {
        assert (protocol != null);
        if (account == null) {
            return protocolToDefaultAccount.get(protocol);
        }
        else {
            return account;
        }
    }
    public Params getAccountParamsIfExists(String protocol, String account) {
        assert(protocol != null);

        ProtocolCredentials creds = protocolCredentials.get(protocol);
        if (creds == null) {
            return null;
        }
        if (account == null) {
            account = protocolToDefaultAccount.get(protocol);
        }
        Params p = creds.get(account);

        return p;
    }

    // Private
    private void checkCredentials(Object o, String protocol) {
        if (o == null) {
            if (protocol.equals("s3") || protocol.equals("gs")) {
                throw ue(protocol
                         , "You can add an account using 'dctc add-account'.");
            }
            else {
                throw ue(protocol
                         , "Please edit configuration file: "
                         + GlobalConf.confPath());
            }
        }
    }
    private UserException ue(String protocol, String msg) {
        return new UserException("No credentials for protocol '"
                                 + protocol
                                 + "'. "
                                 + msg);
    }

    @SuppressWarnings("serial")
    public static class ProtocolCredentials extends HashMap<String, Params> {}

    // Attributes
    private Map<String, String> protocolToDefaultAccount
        = new HashMap<String, String>();
    private Map<String, ProtocolCredentials> protocolCredentials
        = new HashMap<String, ProtocolCredentials>();
}
