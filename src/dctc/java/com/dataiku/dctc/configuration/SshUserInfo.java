package com.dataiku.dctc.configuration;

import com.jcraft.jsch.UserInfo;

public class SshUserInfo implements UserInfo {
    public SshUserInfo(String secret) {
        this.password = secret;
    }
    @Override
    public String getPassphrase() {
        if (password == null) {
            String prompt = new String(System.console().readPassword());
            return prompt;
        }
        return password;
    }
    @Override
    public String getPassword() {
        if (password != null) {
            return password;
        }
        return new String(System.console().readPassword());
    }
    @Override
    public boolean promptPassphrase(String arg0) {
        if (password == null) {
            System.out.print(arg0 + ": ");
        }
        return true;
    }
    @Override
    public boolean promptPassword(String arg0) {
        if (password == null) {
            System.out.print(arg0 + ": ");
        }
        return true;
    }
    @Override
    public boolean promptYesNo(String arg0) {
        if (GlobalConf.isInteractif()) {
            System.out.print(arg0.substring(0, arg0.length() - 1) + " (yes/no)? ");
            while (true) {
                String response = System.console().readLine();
                if (response.equals("yes")) {
                    return true;
                } else if (response.isEmpty() || response.equals("no")) {
                    return false;
                }
                System.out.print("Please type 'yes' or 'no': ");
            }
        }
        return true;
    }
    @Override
    public void showMessage(String arg0) {
    }

    private String password;
}
