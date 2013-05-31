package com.dataiku.dctc.command;

import org.apache.commons.cli.Options;

public class Version extends Command {
    public String tagline() {
        return "Show DCTC version";
    }
    public String longDescription() {
        return "Show the version of DCTC";
    }

    @Override
    public void perform(String[] args) {
        System.out.println("dctc " + com.dataiku.dctc.configuration.Version.pretty());
    }
    @Override
    protected String proto() {
        return "dctc version";
    }
    @Override
    protected Options setOptions() {
        return new Options();
    }
    @Override
    public String cmdname() {
        return "version";
    }
}