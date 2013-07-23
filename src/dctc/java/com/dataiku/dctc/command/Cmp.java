package com.dataiku.dctc.command;

import static com.dataiku.dip.utils.PrettyString.pquoted;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.dataiku.dctc.AutoGZip;
import com.dataiku.dctc.clo.OptionAgregator;
import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dctc.file.GFile;
import com.dataiku.dip.utils.IndentedWriter;

public class Cmp extends Command {
    public String tagline() {
        return "Compare two files byte by byte.";
    }
    public void longDescription(IndentedWriter printer) {
    }

    // Public
    @Override
    public void perform(String[] args) {
        resetExitCode();
        if (args.length < 2) {
            error("Missing operands", 2);
            return;
        }
        GFile left = build(args[0]);
        GFile right = build(args[1]);
        long lskip = parseSkip(args, 2);
        long rskip = parseSkip(args, 3);

        if (lskip < 0 || rskip < 0) {
            return;
        }

        compare(left, right, lskip, rskip);
    }
    @Override
    public final String cmdname() {
        return "cmp";
    }
    // Protected
    @Override
    protected void setOptions(List<OptionAgregator> opts) {
    }
    @Override
    protected final String proto() {
        return "[OPT...] FILES... [SKIP1 [SKIP2]]";
    }

    // Private
    private void compare(GFile l
                         , GFile r
                         , long lskip
                         , long rskip) {
        InputStream left = open(l);
        InputStream right = open(r);
        if (left == null || right == null) {
            return;
        }
        if (skip(l, left, lskip) || skip(r, right, rskip)) {
            return;
        }

        long count = 0;
        long lineCount = 1;
        while (true) {
            ++count;
            int leftRead = read(l, left);
            int rightRead = read(r, right);
            if (leftRead == -2 || rightRead == -2) {
                return;
            }
            else if (leftRead != rightRead) {
                diff(l, r, count, lineCount);
                return;
            }
            else if (leftRead == -1) {
                return;
            }
            if (leftRead == '\n') {
                ++lineCount;
            }

        }
    }
    private long parseSkip(String[] args, int elt) {
        if (args.length <= elt) {
            return 0;
        }
        String l = args[elt].toLowerCase();
        String unit = l.replaceAll("[0-9]", "");
        String number = l.replaceAll("\\D", "");
        if (!l.equals(number + unit)) {
            error(pquoted(l) + ": is not a number", 2);
            return -1;
        }
        long exp = 1;
        if (!unit.isEmpty()) {
            exp = unit_index.indexOf(unit.substring(0, 1));
            int len = unit.length();
            if (len != 2) {
                exp = 2 << (10 * exp - 1);
            }
            else {
                exp = (long) Math.pow(10, 3 * exp);
            }
        }
        return Long.parseLong(number) * exp;
    }
    private boolean skip(GFile file, InputStream stream, long skip){
        try {
            while (skip != 0) {
                skip -= stream.skip(skip);
            }
            return false;
        }
        catch (IOException e) {
            error("Unexpected error while skiping " + file.givenName(), e, 2);
            return true;
        }
    }
    private void diff(GFile l
                      , GFile r
                      , long count
                      , long lineCount) {
        System.out.println(l.givenName() + " " + r.givenName()
                           + " differ: byte " + count
                           + ", line " + lineCount);
        setExitCode(1);

    }
    private int read(GFile file, InputStream stream) {
        int res;
        try {
            res = stream.read();
        }
        catch (IOException e) {
            error("Unexpected error while reading " + file.givenName(), e, 2);
            return -2;
        }
        return res;
    }
    private InputStream open(GFile file) {
        InputStream stream;
        try {
            stream = AutoGZip.buildInput(file);
        }
        catch (FileNotFoundException e) {
            error(file, "No such file or directory", e, 2);
            return null;
        }
        catch (IOException e) {
            error(file, "Could not open file: " + e.getMessage(), e, 2);
            return null;
        }

        return stream;
    }
    private static String unit_index = " kmgtpezy";
}
