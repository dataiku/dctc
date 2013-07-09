package com.dataiku.dctc.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.dataiku.dctc.clo.Option;
import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dctc.display.Size;
import com.dataiku.dctc.file.FileManipulation;
import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dctc.file.LocalFile;
import com.dataiku.dip.utils.IndentedWriter;
import com.dataiku.dip.utils.IntegerUtils;

public class Du extends Command {
    public String cmdname() {
        return "du";
    }
    public String tagline() {
        return "compute file space usage.";
    }
    public String proto() {
        return "[FILE]...";
    }
    public List<Option> setOptions() {
        List<Option> opts = new ArrayList<Option>();

        opts.add(stdOption('h', "human-readable", "print sizes in human readable format."));
        opts.add(stdOption('s', "summarize", "Display only a total for each argument."));
        opts.add(stdOption('c', "total", "Produce a grand total."));
        opts.add(stdOption('a', "all", "Write counts for all files, not just directories."));
        opts.add(stdOption('d', "max-depth", "Print the total for a directory (or file, with --all) only if it is N or fewer levels below the command line argument; --max-depth=0 is the same as --summarize.", true)); // FIXME: NUMBER

        return opts;
    }
    public void longDescription(IndentedWriter printer) {
        printer.print("Compute file space usage.");
    }
    public void perform(List<GeneralizedFile> args) {
        if (args.size() == 0) {
            args.add(new LocalFile("."));
        }
        long size = 0;
        for (GeneralizedFile arg: args) {
            try {
                size += perform(arg, 0);
            }
            catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        if (hasOption('c')) {
            print(size, "total");
        }
    }
    public long perform(GeneralizedFile arg, int depth) throws IOException {
        long size;
        if (arg.isFile()) {
            size = arg.getSize();
            print(size, arg.givenName());
        }
        else {
            size = arg.getSize();
            boolean first = true;
            for (GeneralizedFile son: arg.grecursiveList()) {
                size += son.getSize();
                if ((all() || son.isDirectory())
                    && FileManipulation.isDirectSon(arg.givenName(), son.givenName(), "/")
                    && !first) {
                    perform(son, depth + 1);
                }
                first = false;
            }
            if (depth <= getDepth()) {
                print(size, arg.givenName());
            }
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
    private int getDepth() {
        if (maxDepth == null) {
            if (hasOption('s')) {
                maxDepth = 0;
            }
            else if (hasOption('d')) {
                maxDepth = IntegerUtils.toInt(getOptionValue('d'));
            }
            else {
                maxDepth = Integer.MAX_VALUE;
            }

        }
        return maxDepth;
    }
    private boolean all() {
        if (all == null) {
            all = hasOption('a');
        }
        return all;
    }
    private boolean humanReadable() {
        if (humanReadable == null) {
            humanReadable = hasOption('h');
        }
        return humanReadable;
    }
    private void print(long size, String name) {
        System.out.println(getPrettySize(size) + "	" + name);
    }

    private Boolean humanReadable;
    private Integer maxDepth;
    private Boolean all;
}
