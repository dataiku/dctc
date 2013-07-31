package com.dataiku.dctc.file;

import static com.dataiku.dip.utils.PrettyString.pquoted;

import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.command.abs.Command.EndOfCommand;
import com.dataiku.dctc.command.policy.HowlPolicy;
import com.dataiku.dctc.command.policy.YellPolicy;
import com.dataiku.dctc.exception.UserException;
import com.dataiku.dctc.file.FileBuilder.Protocol;
import com.dataiku.dip.utils.Params;

/**
 * Builder for FTP files.
 *
 * FTP has quite a versatile syntax ...
 * To avoid possible ambiguities:
 * - FTP has no default credential.
 * - ':' is not possible in credential name
 *
 * Therefore, the possible syntaxes are :
 *
 * ftp://A/B
 * -> Connect in anonymous to A, navigate to path /B
 *
 * FIXME: FALSE, should use the default account. If the comment is
 * right, fix it.
 *
 * ftp://account@/B
 * -> Connect using fully-resolved 'account', navigate to path /B
 *
 * ftp://account@B
 * -> Connect using fully-resolved 'account', navigate to path /B if
          no default path, or default_path/B if one
 *
 *  ftp://user:pwd@A/B
 * -> Connect with user:pwd to A, navigate to path /B
 *
 */
public class FTPFileBuilder extends ProtocolFileBuilder {

    @Override
    public boolean validateAccountParams(String account, Params p) {
        getCheckedPort(p.getParam("port", "22")
                       , new HowlPolicy().withOut(System.err));
        return checkAllowedOnly(account, p, new String[]{"host"
                                                         , "port"
                                                         , "username"
                                                         , "password"
                                                         , "default_path"})
            || checkMandatory(account, p, "host")
            || checkMandatory(account, p, "username")
            || checkMandatory(account, p, "password");
    }

    @Override
    public synchronized FTPFile buildFile(String accountSettings
                                          , String rawPath
                                          , YellPolicy yell) {
        if (accountSettings == null) {
            if (rawPath.isEmpty() || rawPath.equals("/")) {
                throw new UserException("No account given for FTP, host"
                                        + " is mandatory");
            }
            String[/*host/path*/] path = PathManip.split(rawPath, "/", 2);

            return build(path[0], "anonymous", "anonymous", path[1], yell);
        }
        else if (accountSettings.contains(":")) {
            if (rawPath.isEmpty() || rawPath.equals("/")) {
                throw new UserException("No account given for FTP, host is"
                                        + " mandatory");
            }
            String[/*host/path*/] path = PathManip.split(rawPath, "/", 2);
            String[] accountChunks = PathManip.split(accountSettings, ":", 2);

            return build(path[0]
                         , accountChunks[0]
                         , accountChunks[1]
                         , path[1]
                         , yell);
        }
        else {
            Params p
                = getBank().getAccountParams(getProtocol().getCanonicalName()
                                             , accountSettings);
            if (validateAccountParams(accountSettings, p)) {
                throw invalidAccountSettings(accountSettings);
            }

            return new FTPFile(p, translateDefaultPath(p, rawPath));
        }
    }
    public Protocol getProtocol() {
        return Protocol.FTP;
    }

    private FTPFile build(String host
                          , String username
                          , String password
                          , String path
                          , YellPolicy yell) {
        int port = GlobalConstants.FTP_PORT;
        if (host.indexOf(":") != -1) {
            String[/*port/host*/] splittedHost
                = PathManip.invSplit(host, ":", 2);

            port = getCheckedPort(splittedHost[0], yell);
            host = splittedHost[1];
        }
        return new FTPFile(host, username, password, path, port);
    }
    @Override
    public final String fileSeparator() {
        return "/";
    }

    // Private
    private int getCheckedPort(String strPort, YellPolicy yell) {
        // FIXME: Refactor, ugly
        int port;

        try {
            port = Integer.parseInt(strPort);
        }
        catch (NumberFormatException e) {
            yell.yell("ftp file builder"
                      , pquoted(strPort)
                      + "is not a Number. Need a number between 1 and"
                      + " 65536 (included) for the ftp port."
                      , e);
            throw new EndOfCommand();
        }
        if (port < 1 || port > 65535) {
            throw new UserException("A port must be between 1 and"
                                    + " 65535 (included).");
        }
        return port;
    }
}
