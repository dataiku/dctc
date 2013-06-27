package com.dataiku.dctc.command;

import static com.dataiku.dip.utils.PrettyString.eol;
import static com.dataiku.dip.utils.PrettyString.scat;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        if (args.length == 0) {
            for (Entry<String, String> alia: alias.getAlias().entrySet()) {
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
                FileWriter fw = new FileWriter("/home/vash/.dctcrc", true);
                fw.write(sb.toString() + eol());
                fw.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
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
