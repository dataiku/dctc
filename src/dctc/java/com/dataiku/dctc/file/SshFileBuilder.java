package com.dataiku.dctc.file;

import static com.dataiku.dip.utils.PrettyString.pquoted;
import static com.dataiku.dip.utils.PrettyString.scat;

import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.command.policy.YellPolicy;
import com.dataiku.dctc.configuration.SshConfig;
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
    public boolean validateAccountParams(String account, Params p) {
        return checkAllowedOnly(account, p, new String[]{"host", "port", "username"
                                                         , "password", "key", "skip_host_key_check"
                                                         , "identity"})
            || checkMandatory(account, p, "host");
    }

    @Override
    public synchronized GFile buildFile(String account, String rawPath, YellPolicy yell) {
        if (account == null) {
            String[] hostPath = PathManip.split(rawPath, ":", 2);
            Params p = getBank().getAccountParamsIfExists(getProtocol().getCanonicalName(), account);
            return new SshFile(sshConfig, hostPath[0], hostPath[1], p);
        }

        Params p = getBank().getAccountParamsIfExists(getProtocol().getCanonicalName(), account);
        if (p != null) {
            if (validateAccountParams(account, p)) {
                throw invalidAccountSettings(account);
            }
            String[] path = PathManip.split(rawPath, ":", 2, false);
            if (path[0].isEmpty()) {
                path[0] = p.getMandParam("host");
            }
            if (path[1] == null) {
                path[1] = ".";
            }

            return new SshFile(sshConfig, path[0], path[1], p);
        }
        else {
            String[] user = PathManip.split(account, ":", 2, false);
            String[] path = PathManip.split(rawPath, ":", 2);
            if (PathManip.contains(path[0], "[")) {
                throw new IllegalArgumentException("Doesn't manage ipv6 address");
            }
            if (path[0].isEmpty()) {
                throw new IllegalArgumentException(scat("Missing host. Maybe you meant"
                                                        , pquoted(account)
                                                        , "as an account, but it doesn't exist."));
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
    public void setSshConfig(SshConfig sshConfig) {
        this.sshConfig = sshConfig;
    }

    private SshConfig sshConfig;
}
