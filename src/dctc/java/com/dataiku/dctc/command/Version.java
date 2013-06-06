package com.dataiku.dctc.command;

import org.apache.commons.cli.Options;

import com.dataiku.dip.utils.IndentedWriter;

public class Version extends Command {
    public String tagline() {
        return "Show DCTC version";
    }
    public void longDescription(IndentedWriter printer) {
        printer.print("Show the version of DCTC");
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