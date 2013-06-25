package com.dataiku.dctc.command.abs;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.dataiku.dctc.DCTCLog;
import com.dataiku.dctc.Globbing;
import com.dataiku.dctc.Main;
import com.dataiku.dctc.configuration.GlobalConf;
import com.dataiku.dctc.file.FileBuilder;
import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dip.utils.IndentedWriter;

import static com.dataiku.dip.utils.PrettyString.scat;

public abstract class Command {
    // The goal of this exception is to abort a command by bubbling up to main
    public static class EndOfCommand extends Error {
        private static final long serialVersionUID = 1L;
    }

    // Description of what the command does
    public abstract String cmdname();
    public abstract String tagline();
    protected abstract String proto();
    public abstract void longDescription(IndentedWriter printer);

    // Abstract methods
    protected abstract Options setOptions();
    protected List<String> getFileArguments(String[] list) {
        return Arrays.asList(list);
    }
    public void perform(String[] args) {
        resetExitCode();
        // Default implementation could be override
        List<GeneralizedFile> arguments = getArgs(args);
        if (arguments != null) {
            perform(arguments);
        }
    }
    protected void longOpt(Options opt, String desc, String longName,
                           String shortName, String paramName) {
        OptionBuilder.withDescription(desc);
        OptionBuilder.hasArg();
        OptionBuilder.withArgName(paramName);
        OptionBuilder.withLongOpt(longName);
        opt.addOption(OptionBuilder.create(shortName));
    }
    public void perform(List<GeneralizedFile> args) {
        throw new NotImplementedException();
    }

    /** Prints the usage in case of bad usage by the user */
    public void usage() {
        if (getExitCode() != 0) {
            System.setOut(System.err);
        }
        initOptions();
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(scat("dctc"
                                 , cmdname()
                                 , proto()), opt);

    }
    public FileBuilder getFileBuilder() {
        return builder;
    }
    public Command setFileBuilder(FileBuilder builder) {
        this.builder = builder;
        return this;
    }

    public int getExitCode() {
        return exitCode;
    }
    public void perform(GeneralizedFile[] args) {
        resetExitCode();
        perform(Arrays.asList(args));
    }

    // Protected methods

    protected void parseCommandLine(String[] shellargs) {
        initOptions();
        CommandLineParser parser = new PosixParser();
        try {
            line = parser.parse(opt, shellargs);
        } catch (ParseException exp) {
            error(exp.getMessage(), 1);
            usage();
            throw new EndOfCommand();

        }
        if (line.hasOption("help")) {
            Main.commandHelp(this, new IndentedWriter());
            throw new EndOfCommand();
        }
        if (line.hasOption("v")) {
            Logger.getRootLogger().setLevel(Level.INFO);
        }
        if (line.hasOption("V")) {
            Logger.getRootLogger().setLevel(Level.DEBUG);
            DCTCLog.setLevel(DCTCLog.Level.DEBUG);
        }
    }

    protected List<GeneralizedFile> getArgs(String[] shellargs) {
        parseCommandLine(shellargs);
        List<GeneralizedFile> args = new ArrayList<GeneralizedFile>();
        for (String arg: getFileArguments(line.getArgs())) {
            GeneralizedFile garg = build(arg);
            if (GlobalConf.getResolveGlobbing()) {
                try {
                    args.addAll(Globbing.resolve(garg, false));
                } catch (IOException e) {
                    error(garg.givenName(),
                          "Couldn't resolve globbing for " + garg.givenName(), e, 2);
                    args.add(garg);
                }
            } else {
                args.add(garg);
            }
        }
        return args;
    }
    protected void setExitCode(int exitCode) {
        this.exitCode = Math.max(exitCode, this.exitCode);
    }


    protected void error(String msg, int exitCode) {
        DCTCLog.error(cmdname(), msg);
        setExitCode(exitCode);
    }
    protected void error(String msg, Throwable exception, int exitCode) {
        DCTCLog.error(cmdname(), msg, exception);
        setExitCode(exitCode);
    }
    protected void error(String fileName, String msg, Throwable exception, int exitCode) {
        error("`" + fileName + "': " + msg, exception, exitCode);
    }
    protected void error(String fileName, String msg, int errorCode) {
        error("`" + fileName + "': " + msg, errorCode);
    }

    protected void errorWithHandlingOfKnownExceptions(String fileName, String msg, Throwable exception, int exitCode) {
        msg = (fileName == null ? msg :  ("`" + fileName + "': " + msg));
        if (exception instanceof UnknownHostException) {
            error(msg + ": Unknown host '" + exception.getMessage() + "'", exitCode);
        } else {
            error(msg, exception, exitCode);
        }
    }

    protected void warn(String msg) {
        error(msg, 0);
    }
    protected void warn(String msg, Throwable exception) {
        error(msg, exception, 0);
    }

    protected GeneralizedFile build(String path) {
        return getFileBuilder().buildFile(path);
    }
    protected List<GeneralizedFile> build(String[] paths) {
        GeneralizedFile[] array = getFileBuilder().buildFile(paths);
        return Arrays.asList(array);
    }
    protected boolean hasOption(String opt) {
        return line != null && line.hasOption(opt);
    }
    protected String getOptionValue(String opt) {
        return line.getOptionValue(opt);
    }
    protected String getOptionValue(String opt, String defaultValue) {
        return hasOption(opt) ? getOptionValue(opt) : defaultValue;
    }
    protected void resetExitCode() {
        this.exitCode = 0;
    }

    // Private methods
    private void initOptions() {
        if (opt == null) {
            opt = setOptions();
            OptionBuilder.withDescription("Display this help message.");
            OptionBuilder.withLongOpt("help");
            opt.addOption(OptionBuilder.create());
            opt.addOption("v", "verbose", false, "Enable verbose logging");
            opt.addOption("V", "VV", false, "Enable debug logging");
        }
    }

    // Attributes
    private CommandLine line;
    private int exitCode;
    private FileBuilder builder;
    private Options opt;
}
