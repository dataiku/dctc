package com.dataiku.dctc.command;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.cli.Options;

import com.dataiku.dctc.AutoGZip;
import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dip.utils.IndentedWriter;

public class Cmp extends Command {
    public String tagline() {
        return "Compare two files byte by byte.";
    }
    public void longDescription(IndentedWriter printer) {
    }

    // Public
    @Override
    public void perform(List<GeneralizedFile> args) {
        if (args.size() != 2) {
            error("Missing operands", 2);
        }

        compare(args.get(0), args.get(1));
    }
    @Override
    public final String cmdname() {
        return "cmp";
    }
    // Protected
    @Override
    protected Options setOptions() {
        Options options = new Options();
        return options;
    }
    @Override
    protected final String proto() {
        return "dctc cmp [OPT...] FILES...";
    }

    // Private
    private void compare(GeneralizedFile l, GeneralizedFile r) {
        InputStream left = open(l);
        InputStream right = open(r);
        if (left == null || right == null) {
            return;
        }

        long count = 0;
        while (true) {
            ++count;
            int leftRead = read(l, left);
            int rightRead = read(r, right);
            if (leftRead == -2 || rightRead == -2) {
                return;
            }
            else if (leftRead != rightRead) {
                diff(l, r, count);
                return;
            }
            else if (leftRead == -1) {
                return;
            }

        }

    }
    private void diff(GeneralizedFile l, GeneralizedFile r, long count) {
        System.out.println(l.givenName() + " " + r.givenName() + " differ: byte " + count);
        setExitCode(1);

    }
    private int read(GeneralizedFile file, InputStream stream) {
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
    private InputStream open(GeneralizedFile file) {
        InputStream stream;
        try {
            stream = AutoGZip.buildInput(file);
        }
        catch (FileNotFoundException e) {
            error (file.givenName(), "No such file or directory", 2);
            return null;
        }
        catch (IOException e) {
            error(file.givenName(), "Could not open file: " + e.getMessage(), e, 2);
            return null;
        }

        return stream;
    }
}
