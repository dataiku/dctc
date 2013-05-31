package com.dataiku.dctc.file;

import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.exception.UserException;
import com.dataiku.dctc.file.FileBuilder.Protocol;
import com.dataiku.dip.utils.Params;

/** Builder for SSH files
 *
 * ssh://A/B
 *  --> Forbidden syntax
 *
 * ssh://user@A/B
 *  --> Connect to A, navigate to path /B, prompts password
 *
 * ssh://account@B --> Connect using fully-resolved 'account',
 * navigate to path /B if no default path, or default_path/B if one
 */
public class SshFileBuilder extends ProtocolFileBuilder {
    @Override
    public void validateAccountParams(String account, Params p) {
        checkAllowedOnly(account, p, new String[]{"host", "port", "username",
                                                  "password", "key", "passphrase",
                                                  "skip_host_key_check"});
        checkMandatory(account, p, "host");
        checkMandatory(account, p, "username");
    }

    @Override
    public synchronized GeneralizedFile buildFile(String account, String rawPath) {
        if (account == null) {
            throw new UserException("For SSH, you must specify either ssh://user@host/path or ssh://conf_account@/path");
        }

        Params p = bank.getAccountParamsIfExists(getProtocol().getCanonicalName(), account);
        if (p != null) {
            validateAccountParams(account, p);
            String[] path = FileManipulation.split(rawPath, ":", 2, false);
            if (path[1] == null) {
                path[1] = path[0];
                path[0] = "";
            }
            if (path[0].isEmpty()) {
                path[0] = p.getMandParam("host");
            }
            if (p.hasParam("password")) {
                return new SshFile(path[0],
                                   p.getMandParam("username"),
                                   p.getParam("password"),
                                   path[1],
                                   p.getShortParam("port", GlobalConstants.SSH_PORT),
                                   p.getBoolParam("skip_host_key_check", false));
            } else {
                return new SshFile(path[0],
                                   p.getMandParam("username"),
                                   p.getMandParam("key"),
                                   p.getParam("passphrase"),
                                   path[1],
                                   p.getShortParam("port", GlobalConstants.SSH_PORT),
                                   p.getBoolParam("skip_host_key_check", false));
            }
        } else {
            String[] user = FileManipulation.split(account, ":", 2, false);
            String[] path = FileManipulation.split(rawPath, ":", 2);
            if (FileManipulation.contains(path[0], "[")) {
                throw new IllegalArgumentException("Doesn't manage ipv6 address");
            }

            return new SshFile(path[0], user[0], user[1], path[1], GlobalConstants.SSH_PORT, false);
        }
    }
    public Protocol getProtocol() {
        return Protocol.SSH;
    }
    @Override
    public final String fileSeparator() {
        return "/";
    }
}
/*
ssh://ssh@localhost:/home/vash
ssh://ssh@/home/vash

*/
