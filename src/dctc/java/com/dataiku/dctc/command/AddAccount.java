package com.dataiku.dctc.command;

import static com.dataiku.dip.utils.PrettyString.eol;
import static com.dataiku.dip.utils.PrettyString.scat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.Options;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dctc.configuration.Configuration;
import com.dataiku.dctc.configuration.CredentialProviderBank;
import com.dataiku.dctc.configuration.GlobalConf;
import com.dataiku.dctc.display.Interactive;
import com.dataiku.dctc.exception.UserException;
import com.dataiku.dctc.file.GSFile;
import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dctc.file.S3File;
import com.dataiku.dip.utils.IndentedWriter;
import com.dataiku.dip.utils.PrettyString;

public class AddAccount extends Command {
    public String tagline() {
        return "Add an account to the DCTC configuration.";
    }
    public void longDescription(IndentedWriter printer) {
        printer.print(scat("Add an account to the DCTC file configuration using a command-line wizard."
                           ,"Supported protocols are 's3' and 'gs'"));
    }

    @Override
    public void perform(String[] args) {
        resetExitCode();
        parseCommandLine(args);
        if (args.length == 0) {
            error("Missing argument: protocol (one of 's3' or 'gs')" + eol(), 2);
            usage();
            return;
        }
        if (!PrettyString.isInteractif()) {
            throw new UserException("add-account requires dctc to run in interactive mode");
        }

        String proto = args[0].toLowerCase();
        String account = null;
        if (args.length > 1) {
            account = args[1];
        }

        CredentialProviderBank bank = new CredentialProviderBank();

        if (bank.getProtocolCredentials(proto) != null) {
            if (account == null) {
                throw new UserException("At least an account already exists for protocol '" + proto
                                        + "'. You must specify an explicit "
                                        + "account name for your new account");
            }
        }
        if (account == null) {
            System.err.println("You haven't enter an account name.");
            account = Interactive.askString("Please enter the account name: ");
        }
        if (account.isEmpty()) {
            account = "default_account";
        }

        if (bank.getAccountParamsIfExists(proto, account) != null) {
            throw new UserException("Account '" + account +  "' already exists for protocol '"
                                    + proto + "'.");
        }

        Map<String, String> parameters;
        GeneralizedFile root = null;
        if (proto.equals("s3")) {
            while (true) {
                parameters = ask("Please enter your AWS access key|access_key"
                                 , "Please enter your AWS secret key|secret_key");

                printChecking();

                root = new
                    S3File("/", new AmazonS3Client(new BasicAWSCredentials(parameters.get("access_key")
                                                                           , parameters.get("secret_key"))));
                break;
            }
        }
        else if (proto.equals("gs")) {
            while (true) {
                parameters = ask("Please enter your Google service account email|mail"
                                 , "Please enter the path on disk of your private key file|key_path");

                printChecking();
                root = new GSFile(parameters.get("mail")
                                  , parameters.get("key_path"), "/");
                break;
            }
        }
        else {
            error(scat("Unknown protocol", proto), 1);
            return;
        }
        try {
            int nbBuckets = root.glist().size();
            System.err.println(scat("\rOK, listed", nbBuckets, "buckets in your", proto, "account."));
        }
        catch (Exception e) {
            System.err.println("\rCould not list your buckets using these credentials:"
                               + eol() + e.getMessage());
        }

        System.err.println("Updating configuration file: " + GlobalConf.confPath());
        try {
            configuration.appendNewProtocol("add-account", proto, account, parameters);
        }
        catch (IOException e) {
            error(GlobalConf.confPath(), "Couldn't write in the configuration file", e, 3);
        }
    }
    private void printChecking() {
        System.err.println("Testing if these credentials work.");
        System.err.print("Please wait...");
        System.err.flush();
    }
    private Map<String, String> ask(String... asks) {
        Map<String, String> res = new HashMap<String, String>();
        for (String ask: asks) {
            int pipe = ask.lastIndexOf("|");
            assert pipe != -1
                : "pipe != -1";

            String rep;
            do {
                rep = Interactive.askString(ask.substring(0, pipe) + ": ");
            } while (rep.isEmpty());
            res.put(ask.substring(pipe + 1), rep);
        }

        return res;
    }
    @Override
    protected String proto() {
        return "protocol [account-name]";
    }
    @Override
    protected Options setOptions() {
        return new Options();
    }
    @Override
    public String cmdname() {
        return "add-account";
    }
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    // Attributes
    private Configuration configuration;
}
