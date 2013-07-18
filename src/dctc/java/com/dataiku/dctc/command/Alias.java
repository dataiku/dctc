package com.dataiku.dctc.command;

import static com.dataiku.dip.utils.PrettyString.scat;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dataiku.dctc.clo.OptionAgregator;
import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dctc.configuration.GlobalConf;
import com.dataiku.dctc.configuration.StructuredConf;
import com.dataiku.dip.utils.IndentedWriter;

public class Alias extends Command {
    public String cmdname() {
        return "alias";
    }
    public String tagline() {
        return "Display the alias";
    }
    public void setOptions(List<OptionAgregator> opts) {
    }
    public String proto() {
        return "";
    }
    public void longDescription(IndentedWriter printer) {
        printer.print("Display the alias");
    }

    public void perform(String[] args) {
        if (args.length == 0) {
            for (Entry<String, String> alia: conf.getAlias().getAlias().entrySet()) {
                System.out.println(alia.getKey() + "=\"" + alia.getValue() + "\"");
            }
        }
        else {
            Map<String, String> newAlias = new HashMap<String, String>();

            int sep = args[0].indexOf("=");
            String aliasName;
            if (sep != -1) {
                aliasName = args[0].substring(0, sep);
                args[0] = args[0].substring(sep + 1);
            }
            else {
                aliasName = args[0];
                args[0] = "";
            }
            newAlias.put(aliasName, scat((Object[]) args).trim());

            try {
                conf.getConf().appendCustomSection("alias", "alias", newAlias);
            }
            catch (IOException e) {
                error("Could not write to: " + GlobalConf.confPath(), e, 2);
            }
        }
    }

    // Getters/Setters
    public StructuredConf getConf() {
        return conf;
    }
    public void setConf(StructuredConf conf) {
        this.conf = conf;
    }
    public Alias withConf(StructuredConf conf) {
        this.conf = conf;
        return this;
    }
    // Attributes
    private StructuredConf conf;
}
