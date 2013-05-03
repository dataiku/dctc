package com.dataiku.dctc.command;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.Options;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.dataiku.dctc.configuration.CredentialProviderBank;
import com.dataiku.dctc.configuration.GlobalConf;
import com.dataiku.dctc.display.Interactive;
import com.dataiku.dctc.exception.UserException;
import com.dataiku.dctc.file.GoogleFile;
import com.dataiku.dctc.file.S3File;

public class AddAccount extends Command {
    public String tagline() {
        return "Add an account to the DCTC configuration";
    }
    public String longDescription() {
        return "Add an account to the DCTC file configuration using a command-line wizard. Supported protocols are 's3' and 'gs'";
    }
    
    @Override
    public void perform(String[] args) throws IOException {
        if (args.length == 0) {
            usage();
            error("Missing argument: protocol (one of 's3' or 'gs')", 2);
            return;
        }
        if (!GlobalConf.isInteractif()) {
            throw new UserException("add-account requires dctc to run in interactive mode");
        }

        String proto = args[0];
        String account = null;
        if (args.length > 1) {
            account = args[1];
        }

        CredentialProviderBank bank = new CredentialProviderBank(configuration);

        if (bank.getProtocolCredentials(proto) != null) {
            if (account == null) {
                throw new UserException("At least an account already exists for protocol '" + proto
                                        + "'. You must specify an explicit "
                                        + "account name for your new account");
            }
        }
        if (account == null) {
            account = "default_account";
        }

        if (bank.getAccountParamsIfExists(proto, account) != null) {
            throw new UserException("Account '" + account +  "' already exists for protocol '"
                                    + proto + "'.");
        }

        if (proto.equalsIgnoreCase("s3")) {
            while (true) {
                String accessKey = Interactive.askString("Please enter your AWS access key: ");
                if (accessKey.length() < 8) {
                    System.err.println("Invalid AWS access key");
                    continue;
                }
                String secretKey = Interactive.askString("Please enter your AWS secret key: ");
                if (secretKey.length() < 16) {
                    System.err.println("Invalid AWS secret key");
                    continue;
                }
                System.err.println("Testing if these credentials work.");

                System.err.print("Please wait...");
                try {
                    S3File s3File = new S3File("/", new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey)));
                    int nbuckets = s3File.glist().size();
                    System.err.println("\rOK, listed " + nbuckets + " buckets in your S3 account.");
                }
                catch (Exception e) {
                    System.err.println("\rCould not list your buckets using these credentials. AWS said: " + e.getMessage());
                    continue;
                }
                configuration.put("s3", account, "access_key", accessKey);
                configuration.put("s3", account, "secret_key", secretKey);
                System.err.println("Updating configuration file : " + GlobalConf.confFile());
                configuration.write(GlobalConf.confFile());
                return;
            }
        }
        else if (proto.equalsIgnoreCase("gs")) {
            System.err.println("You can find your Google service account email in the 'API Access' tab of the API Console, in 'Service Account'");
            while (true) {
                String email = Interactive.askString("Please enter your Google service account email: ");
                if (email.length() < 8 || !email.contains("@")) {
                    System.err.println("Invalid Google service account email");
                    continue;
                }
                System.err.println("Your private key file is generally a .p12 file");
                String keyPath = Interactive.askString("Please enter the path on disk of your private key file: ");
                if (!new File(keyPath).exists()) {
                    System.err.println("Invalid key file path: No such file.");
                    continue;
                }
                System.err.println("Testing if these credentials work.");

                System.err.print("Please wait...");
                try {
                    GoogleFile gfile = new GoogleFile(email, keyPath, "/");
                    int nbuckets = gfile.glist().size();
                    System.err.println("\rOK, listed " + nbuckets + " buckets in your GCS account");
                } catch (Exception e) {
                    System.err.println("\rCould not list your buckets using these credentials. GCS said: " + e.getMessage());
                    continue;
                }
                configuration.put("gs", account, "mail", email);
                configuration.put("gs", account, "key_path", keyPath);
                System.err.println("Updating configuration file: " + GlobalConf.confFile());
                configuration.write(GlobalConf.confFile());
                return;
            }
        }
    }

    @Override
    protected String proto() {
        return "dctc add-account protocol [account-name]";
    }

    @Override
    protected Options setOptions() {
        return new Options();
    }

    @Override
    public String cmdname() {
        return "add-account";
    }
}
