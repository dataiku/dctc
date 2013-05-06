package com.dataiku.dctc.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;

import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.file.GeneralizedFile;

public class Tail extends Command {
    public String tagline() {
        return "Output the end of files";
    }
    public String longDescription() {
        return "Output the last N lines or bytes of the input files";
    }

    
    @Override
    public String cmdname() {
        return "tail";
    }
    @Override
    protected String proto() {
        return "dctc tail [OPTIONS...] [FILE...]";
    }
    @Override
    protected Options setOptions() {
        Options opt = new Options();

        longOpt(opt, "Output the last K bytes", "bytes", "b", "K");
        longOpt(opt, "Output the last K lines", "lines", "n", "K");
        return opt;
    }
    @Override
    public void perform(List<GeneralizedFile> args) {
        if (nbByte() != -1) {
            performByte(args);
        } else {
            performLine(args);
        }
    }
    private void performLine(List<GeneralizedFile> args) {
        for (GeneralizedFile arg: args) {
            if (args.size() > 1) {
                header(arg);
            }
            if (arg.canGetLastLines()) {
                try {
                    IOUtils.copyLarge(arg.getLastLines(nbLine()), System.out);
                } catch (IOException e) {
                    error(arg.givenName(), e.getMessage(), 1);
                }
            } else if (arg.canGetPartialFile()) {
                long size;
                try {
                    size = arg.getSize();
                } catch (IOException e) {
                    error(arg.givenName(), e.getMessage(), 1);
                    continue;
                }
                String line = "";

                while (size >= 0 && line.split("\n").length <= nbLine()) {

                    StringWriter writer = new StringWriter();
                    try {
                        size -= GlobalConstants.FIVE_MIO;
                        IOUtils.copy(arg.getRange(size, GlobalConstants.FIVE_MIO), writer, "UTF-8");
                    } catch (IOException e) {
                        error(arg.givenName(), e.getMessage(), 1);
                    }
                    line = writer.toString() + line;

                }
                String[] lines = line.split("\n");
                for (int i = lines.length - (int) nbLine(); i < lines.length; ++i) {
                    System.out.println(lines[i]);
                }
            } else {
                String[] buf = new String[(int) nbLine()];
                int idx = 0;
                BufferedReader in;
                try {
                    in = new BufferedReader(new InputStreamReader(arg.inputStream()));
                    String line = null;

                    while((line = in.readLine()) != null) {
                        buf[idx] = line;
                        idx = (idx + 1) % (int) nbLine();
                    }
                } catch (IOException e) {
                    error(arg.givenName(), "error", e, 2);
                }
                for (long i = 0; i < nbLine(); ++i) {
                    System.out.println(buf[(int) (idx + i) % (int) nbLine]);
                }
            }
        }
    }
    private void performByte(List<GeneralizedFile> args) {
        for (GeneralizedFile arg: args) {
            if (args.size() > 1) {
                header(arg);
            }
            if (arg.canGetPartialFile()) {
                try {
                    IOUtils.copyLarge(arg.getLastBytes(nbByte()), System.out);
                } catch (IOException e) {
                    error(arg.givenName(), e.getMessage(), 1);
                }
            } else {
                try {
                    InputStream input = arg.inputStream();
                    input.skip(arg.getSize() - nbByte());
                    IOUtils.copyLarge(input, System.out);
                } catch (IOException e) {
                    error(arg.givenName(), e.getMessage(), 1);
                }
            }
        }
    }
    private void header(GeneralizedFile arg) {
        System.out.print("===> ");
        System.out.print(arg.givenName());
        System.out.println(" <===");
    }
    public long nbLine() {
        if (nbLine == -1) {
            if (hasOption("line")) {
                nbLine = Integer.parseInt(getOptionValue("line"));
            } else {
                nbLine = 10;
            }
        }
        return nbLine;
    }
    public Tail nbLine(long nbLine) {
        this.nbLine = nbLine;
        return this;
    }
    public long nbByte() {
        if (nbByte == -1) {
            if (hasOption("byte")) {
                nbByte = Integer.parseInt(getOptionValue("byte"));
            }
        }
        return nbByte;
    }
    public Tail nbByte(long nbByte) {
        this.nbByte = nbByte;
        return this;
    }

    private long nbLine = -1;
    private long nbByte = -1;
}
