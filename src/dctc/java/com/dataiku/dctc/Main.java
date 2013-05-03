package com.dataiku.dctc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.dataiku.dctc.DCTCLog.Mode;
import com.dataiku.dctc.command.AddAccount;
import com.dataiku.dctc.command.Cat;
import com.dataiku.dctc.command.Command;
import com.dataiku.dctc.command.Cp;
import com.dataiku.dctc.command.Dispatch;
import com.dataiku.dctc.command.Edit;
import com.dataiku.dctc.command.Find;
import com.dataiku.dctc.command.Grep;
import com.dataiku.dctc.command.Head;
import com.dataiku.dctc.command.Ls;
import com.dataiku.dctc.command.Mkdir;
import com.dataiku.dctc.command.Mv;
import com.dataiku.dctc.command.Rm;
import com.dataiku.dctc.command.Rmdir;
import com.dataiku.dctc.command.Sync;
import com.dataiku.dctc.command.Tail;
import com.dataiku.dctc.configuration.Configuration;
import com.dataiku.dctc.configuration.CredentialProviderBank;
import com.dataiku.dctc.configuration.GlobalConf;
import com.dataiku.dctc.exception.UserException;
import com.dataiku.dctc.file.FileBuilder;
import com.dataiku.dip.utils.StdOut;

public class Main {
    private static void globalUsage(int exitCode) {
        if (exitCode != 0) {
            System.setOut(System.err);
        }
        System.out.println("usage: dctc cmd [CMDARGUMENTS...]");
        System.out.println();
        System.out.println("Available commands are:");
        for (Command cmd: cmds.values()) {
            System.out.println("- dctc " + cmd.cmdname() + " -- " + cmd.tagline());
        }
        System.exit(exitCode);
    }
    private static void commandHelp(int exitCode, String command) {
        if (exitCode != 0) {
            System.setOut(System.err);
        }
        for (Command cmd: cmds.values()) {
            if (cmd.cmdname().equals(command)) {
                commandHelp(cmd);
                System.exit(exitCode);
            }
        }
        System.out.println("Command not found: " + command);
        globalUsage(1);
    }
    public static void commandHelp(Command cmd) {
        System.out.println("dctc " + cmd.cmdname() + " -- " + cmd.tagline());
        System.out.println();
        System.out.println("  " + cmd.longDescription().replace("\n", "\n  "));
        System.out.println();
        cmd.usage();
    }

    static Logger logger = Logger.getLogger(Main.class);
    public static void setLogger() {
        Logger.getRootLogger().removeAllAppenders();
        ConsoleAppender ca = new ConsoleAppender(new PatternLayout("[%r] [%t] [%-5p] [%c] %x - %m%n"));
        ca.setName("console");
        ca.setTarget(ConsoleAppender.SYSTEM_ERR);
        ca.activateOptions();
        Logger.getRootLogger().addAppender(ca);
        Logger.getRootLogger().setLevel(Level.INFO);
    }
    private static void addCmd(Command cmd) {
        cmds.put(cmd.cmdname(), cmd);
    }
    private static void fillCommand() {
        addCmd(new AddAccount());
        addCmd(new Cat());
        addCmd(new Cp());
        addCmd(new Dispatch());
        addCmd(new Edit());
        addCmd(new Find());
        addCmd(new Grep());
        addCmd(new Head());
        addCmd(new Ls());
        addCmd(new Mkdir());
        addCmd(new Mv());
        addCmd(new Rmdir());
        addCmd(new Rm());
        addCmd(new Sync());
        addCmd(new Tail());
    }
    public static void atExit() {
        Runtime.getRuntime().addShutdownHook
            (
             new Thread()
             {
                 public void run() {
                     System.out.flush();
                     System.err.flush();
                 }
             } );
    }
    public static void atBegin() {
//        System.setOut(new StdOut(System.out));
//        System.setErr(new StdOut(System.err));
    }
    public static void main(String[] args) {
        atExit();
        atBegin();
        try {
            setLogger();
            fillCommand();
            DCTCLog.setMode(Mode.STDERR);

            Configuration conf = new Configuration(GlobalConf.confFile());
            CredentialProviderBank bank = new CredentialProviderBank(conf);
            FileBuilder builder = new FileBuilder(bank);

            if (args.length >= 1) {
                String usercmd = args[0];
                String[] cmdargs = new String[args.length - 1];
                System.arraycopy(args, 1, cmdargs, 0, args.length - 1);
                if (usercmd.equals("help")) {
                    if (cmdargs.length > 0) {
                        commandHelp(0, cmdargs[0]);
                    } else {
                        globalUsage(0);
                    }
                }
                if (cmds.containsKey(usercmd)) {
                    Command cmd = cmds.get(usercmd);

                    cmd.setConfiguration(conf);
                    cmd.setFileBuilder(builder);
                    try {
                        cmd.perform(cmdargs);
                    } catch (IOException e) {
                        System.err.println("dctc: " + e.getMessage());
                    } catch (Command.EndOfCommandException e) {}
                    System.exit(cmd.getExitCode());
                }
                System.err.println("Unknown command : " + usercmd);
            }
            globalUsage(1);
        } catch (UserException e) {
            System.err.println("dctc: ERROR: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            DCTCLog.error("main", "Command failed", e);
        }
    }

    private static Map<String, Command> cmds = new HashMap<String, Command>();
}
