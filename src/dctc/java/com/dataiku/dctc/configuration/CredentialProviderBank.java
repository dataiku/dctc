package com.dataiku.dctc.configuration;

import java.util.HashMap;
import java.util.Map;

import com.dataiku.dctc.exception.UserException;
import com.dataiku.dctc.file.FileBuilder.Protocol;
import com.dataiku.dip.utils.ErrorContext;
import com.dataiku.dip.utils.Params;

public class CredentialProviderBank {
    public CredentialProviderBank() {
    }
    public CredentialProviderBank(Configuration conf) {
        add(conf);
    }

    private void addCredentiaParam(String protocol, String account, String key, String value) {
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
        if (accountCred.hasParam(key)) {
            throw ErrorContext.iaef("For protocol '%s' and account '%s', param '%s' specified twice", protocol, account, key);
        }
        accountCred.add(key, value);
    }

    static String[] commandSections = new String[]{"ls"};

    public void add(Configuration conf) {
        for (Map.Entry<String, Map<String, String>> e: conf.getSections().entrySet()) {
            String sectionKey = e.getKey();
            Map<String, String> sectionValues = e.getValue();

            if (sectionKey.equals("global")) {
                setGlobalSettings(sectionValues);
            } else {
                /* Ignore command-specific stuff */
                boolean found = false;
                for (String commandSection : commandSections) {
                    if (sectionKey.equals(commandSection)) { found = true; break; }
                }
                if (found) continue;

                Protocol proto = Protocol.forName(sectionKey);
                String firstAccount = null;

                for (Map.Entry<String, String> entry : sectionValues.entrySet()) {
                    if (entry.getKey().contains(".")) {
                        String[] chunks = entry.getKey().split("\\.");
                        if (firstAccount == null) firstAccount = chunks[0];
                        addCredentiaParam(proto.getCanonicalName(), chunks[0], chunks[1], entry.getValue());
                    } else if (entry.getKey().equals("default")) {
                        protocolToDefaultAccount.put(proto.getCanonicalName(), entry.getValue());
                    } else {
                        throw new UserException("Unexpected parameter " + entry.getKey() + " in section " + sectionKey +", " +
                        		"expected either 'default' or 'account.param' key");
                    }
                }
                if (protocolToDefaultAccount.containsKey(proto.getCanonicalName())) {
                    String defaultAccount = protocolToDefaultAccount.get(proto.getCanonicalName());
                    if (getAccountParamsIfExists(proto.getCanonicalName(), defaultAccount) == null) {
                        throw new UserException("Invalid default account '" + defaultAccount + "' for protocol " + proto.getCanonicalName());
                    }
                }
                if (!protocolToDefaultAccount.containsKey(proto.getCanonicalName())) {
                    /* Take the first one */
                    protocolToDefaultAccount.put(proto.getCanonicalName(), firstAccount);
                }
            }
        }
    }

    public ProtocolCredentials getProtocolCredentials(String protocol) {
        return protocolCredentials.get(protocol);
    }

    @SuppressWarnings("serial")
    public static class ProtocolCredentials extends HashMap<String, Params> {}

    private Map<String, String> protocolToDefaultAccount = new HashMap<String, String>();
    private Map<String, ProtocolCredentials> protocolCredentials = new HashMap<String, ProtocolCredentials>();

    public Params getAccountParams(String protocol, String account) {
        assert(protocol != null);

        ProtocolCredentials creds = protocolCredentials.get(protocol);
        if (creds == null) {
            if (protocol.equals("s3")) {
                throw new UserException("No credentials for protocol '" + protocol + "'. You can add an account using 'dctc add-account'");
            } else {
                throw new UserException("No credentials for protocol '" + protocol + "'. Please edit configuration file: " + GlobalConf.confFile());
            }
        }
        if (account == null) {
            account = protocolToDefaultAccount.get(protocol);
        }
        Params p = creds.get(account);
        if (p == null) {
            if (protocol.equals("s3")) {
                throw new UserException("No credentials for protocol '" + protocol + "' and account '" + account + "'. You can add an account using 'dctc add-account'");
            } else {
                throw new UserException("No credentials for protocol '" + protocol + "' and account '" + account + "'.Please edit configuration file: " + GlobalConf.confFile());
            }
        }
        return p;
    }

    public String getResolvedAccountName(String protocol, String account) {
        assert (protocol != null);
        if (account == null) {
            return protocolToDefaultAccount.get(protocol);
        } else {
            return account;
        }
    }

    public Params getAccountParamsIfExists(String protocol, String account) {
        assert(protocol != null);
        ProtocolCredentials creds = protocolCredentials.get(protocol);
        if (creds == null) return null;
        if (account == null) {
            account = protocolToDefaultAccount.get(protocol);
        }
        Params p = creds.get(account);
        return p;
    }

    private void setGlobalSettings(Map<String, String> settings) {
        GlobalConf.setGlobalSettings(settings);
    }
}
