package com.dataiku.dctc.command;

import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.Options;

import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dctc.display.Size;
import com.dataiku.dctc.file.FileManipulation;
import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dctc.file.LocalFile;
import com.dataiku.dip.utils.IndentedWriter;

public class Du extends Command {
    public String cmdname() {
        return "du";
    }
    public String tagline() {
        return "estimate file space usage.";
    }
    public String proto() {
        return "[FILE]...";
    }
    public Options setOptions() {
        Options opt = new Options();

        opt.addOption("h", "human-readable", false, "print sizes in human readable format.");
        return opt;
    }
    public void longDescription(IndentedWriter printer) {
        printer.print("Estimate file space usage.");
    }
    public void perform(List<GeneralizedFile> args) {
        if (args.size() == 0) {
            args.add(new LocalFile("."));
        }
        for (GeneralizedFile arg: args) {
            perform(arg, 0);
        }
    }
    public long perform(GeneralizedFile arg, int depth) {
        long size;
        try {
            size = arg.getSize();
            boolean first = true;
            for (GeneralizedFile son: arg.grecursiveList()) {
                size += son.getSize();
                if (son.isDirectory()
                    && FileManipulation.isDirectSon(arg.givenName(), son.givenName(), "/")
                    && !first) {
                    perform(son, depth + 1);
                }
                first = false;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        if (depth <= maxDepth) {
            System.out.println(getPrettySize(size) + "	" + arg.givenName());
        }
        return size;
    }
    private String getPrettySize(long size) {
        if (humanReadable()) {
            return Size.getReadableSize(size, "#0.0");
        }
        else {
            return Long.toString(size);
        }
    }
    private boolean humanReadable() {
        if (humanReadable == null) {
            humanReadable = hasOption("h");
        }
        return humanReadable;
    }

    private Boolean humanReadable;
    private int maxDepth = 1;
}
