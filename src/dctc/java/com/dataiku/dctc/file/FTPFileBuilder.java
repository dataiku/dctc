package com.dataiku.dctc.file;

import java.lang.NumberFormatException;

import com.dataiku.dctc.DCTCLog;
import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.exception.UserException;
import com.dataiku.dctc.file.FileBuilder.Protocol;
import com.dataiku.dctc.command.Command.EndOfCommandException;
import com.dataiku.dip.utils.Params;

/**
 * Builder for FTP files.
 *
 * FTP has quite a versatile syntax ...
 * To avoid possible ambiguities,
 *     - FTP has no default credential.
 *     - ':' is not possible in credential name
 *
 * Therefore, the possible syntaxes are :
 *
 *  ftp://A/B
 *     -> Connect in anonymous to A, navigate to path /B
 *  ftp://account@/B
 *     -> Connect using fully-resolved 'account', navigate to path /B
 *  ftp://account@B
 *     -> Connect using fully-resolved 'account', navigate to path /B if no default path, or default_path/B if one
 *  ftp://user:pwd@A/B
 *     -> Connect with user:pwd to A, navigate to path /B
 */
public class FTPFileBuilder extends ProtocolFileBuilder {

    @Override
    public void validateAccountParams(String account, Params p) {
        checkAllowedOnly(account, p, new String[]{"host", "port", "username", "password", "default_path"});
        checkMandatory(account, p, "host");
        checkMandatory(account, p, "username");
        checkMandatory(account, p, "password");
    }

    @Override
    public synchronized FTPFile buildFile(String accountSettings, String rawPath) {
        if (accountSettings == null) {
            if (rawPath.isEmpty() || rawPath.equals("/")) {
                throw new UserException("No account given for FTP, host is mandatory");
            }

            String[/*host/path*/] path = FileManipulation.split(rawPath, "/", 2);
            return build(path[0], "anonymous", "anonymous", path[1]);
        } else if (accountSettings.contains(":")) {
            if (rawPath.isEmpty() || rawPath.equals("/")) {
                throw new UserException("No account given for FTP, host is mandatory");
            }

            String[/*host/path*/] path = FileManipulation.split(rawPath, "/", 2);
            String[] accountChunks = FileManipulation.split(accountSettings, ":", 2);
            return build(path[0], accountChunks[0], accountChunks[1], path[1]);
        } else {
            Params p = bank.getAccountParams(getProtocol().getCanonicalName(), accountSettings);
            // validateAccountParams(accountSettings, p);

            return new FTPFile(p, translateDefaultPath(p, rawPath));
        }
    }
    public Protocol getProtocol() {
        return Protocol.FTP;
    }

    private FTPFile build(String host, String username, String password, String path) {
        int port  = GlobalConstants.FTP_PORT;
        if (host.indexOf(":") != -1) {
            String[/*port/host*/] splittedHost = FileManipulation.invSplit(host, ":", 2);

            try {
                port = Integer.parseInt(splittedHost[0]);
            } catch (NumberFormatException e) {
                DCTCLog.error("FTP file builder", "`"
                        + splittedHost[0]
                                + "' is not a Number. Need a number between 1 and 65536 (included) for the ftp port.");
                throw new EndOfCommandException();
            }
            if (port < 1 || port > 65535) {
                throw new UserException("A port must be between 1 and 65535 (included).");
            }
            host = splittedHost[1];
        }
        return new FTPFile(host, username, password, path, port);
    }
    @Override
    public final String fileSeparator() {
        return "/";
    }

}
