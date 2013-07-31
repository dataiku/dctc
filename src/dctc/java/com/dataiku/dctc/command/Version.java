package com.dataiku.dctc.command;

import java.util.List;

import com.dataiku.dctc.clo.OptionAgregator;
import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dip.utils.IndentedWriter;

public class Version extends Command {
    public String tagline() {
        return "Show DCTC version.";
    }
    public void longDescription(IndentedWriter printer) {
        printer.print("Show the version of DCTC");
    }

    @Override
    public void perform(String[] args) {
        System.out.println("dctc "
                           + com.dataiku.dctc.configuration.Version.pretty());
    }
    @Override
    protected String proto() {
        return "";
    }
    @Override
    protected void setOptions(List<OptionAgregator> opts) {
    }
    @Override
    public String cmdname() {
        return "version";
    }
}
