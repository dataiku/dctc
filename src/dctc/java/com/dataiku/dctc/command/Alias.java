package com.dataiku.dctc.command;

import java.util.Map.Entry;

import org.apache.commons.cli.Options;

import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dip.utils.IndentedWriter;

public class Alias extends Command {
    public String cmdname() {
        return "alias";
    }
    public String tagline() {
        return "Display the alias";
    }
    public Options setOptions() {
        Options opt = new Options();

        return opt;
    }
    public String proto() {
        return "";
    }
    public void longDescription(IndentedWriter printer) {
        printer.print("Display the alias");
    }

    public void perform(String[] args) {
        for (Entry<String, String> alia: alias.getAlias().entrySet()) {
            System.out.println(alia.getKey() + "=\"" + alia.getValue() + "\"");
        }
    }

    // Getters/Setters
    public com.dataiku.dctc.configuration.Alias getAlias() {
        return alias;
    }
    public void setAlias(com.dataiku.dctc.configuration.Alias alias) {
        this.alias = alias;
    }
    public Alias withAlias(com.dataiku.dctc.configuration.Alias alias) {
        this.alias = alias;
        return this;
    }

    // Attributes
    private com.dataiku.dctc.configuration.Alias alias;
}
