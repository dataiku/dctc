package com.dataiku.dctc.command;

import java.util.List;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;

import com.dataiku.dctc.AutoGZip;
import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dip.utils.IndentedWriter;

import static com.dataiku.dip.utils.PrettyString.scat;

/**
 * Cat algorithm based on GeneralizedFile.
 *
 * @see GeneralizedFile
 */
public class Cat extends Command {
    public String tagline() {
        return "Display the content of one or several files";
    }
    public void longDescription(IndentedWriter printer) {
        printer.print(scat("Concatenates the content of all input files to standard output."
                           ,"GZip compressed files are automatically uncompressed"));
    }

    // Public
    @Override
    public void perform(List<GeneralizedFile> args) {
        // No option
        for (GeneralizedFile arg: args) {
            print(arg);
        }
    }
    @Override
    public final String cmdname() {
        return "cat";
    }
    // Protected
    @Override
    protected Options setOptions() {
        Options options = new Options();
        return options;
    }
    @Override
    protected final String proto() {
        return "dctc cat [OPT...] FILES...";
    }

    // Private
    private void print(GeneralizedFile file) {
        InputStream i;
        try {
            i = AutoGZip.buildInput(file);
        } catch (FileNotFoundException e) {
            error (file.givenName(), "No such file or directory", 1);
            return;
        } catch (IOException e) {
            error(file.givenName(), "Could not open file: " + e.getMessage(), e, 2);
            return;
        }
        try {
            IOUtils.copyLarge(i, System.out);
            i.close();
        } catch (IOException e) {
            error("Unexpected error while reading" + file.givenName(), e, 3);
            return;
        }
    }
}
