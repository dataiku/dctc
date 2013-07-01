package com.dataiku.dctc.command;

import static com.dataiku.dip.utils.PrettyString.eol;
import static com.dataiku.dip.utils.PrettyString.scat;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;

import org.apache.commons.cli.Options;

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
        if (args.length == 0) {
            for (Entry<String, String> alia: conf.getAlias().getAlias().entrySet()) {
                System.out.println(alia.getKey() + "=\"" + alia.getValue() + "\"");
            }
        }
        else {
            // Write an alias
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();

            StringBuilder sb = new StringBuilder();
            sb.append(eol());
            sb.append("# Added by dctc alias (" + dateFormat.format(date) + ")." + eol());
            sb.append("[alias]" + eol());
            sb.append(scat((Object[]) args));

            try {
                conf.getConf().appendToConf(sb.toString());
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
