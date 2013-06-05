package com.dataiku.dctc;

import org.apache.log4j.Logger;

public class DCTCLog {
    public enum Mode {
        STDERR,
        LOG4J
    };
    public enum Level {
        ERROR,
        DEBUG
    }

    public static void setMode(Mode newMode) {
        mode = newMode;
    }
    public static void setLevel(Level newLvl) {
        lvl = newLvl;
    }

    public static void info(String module, String fmt, Object...data) {
        if (mode == Mode.STDERR) {
            System.err.println("dctc " + module + ": " + String.format(fmt, data));
        } else if (mode == Mode.LOG4J) {
            Logger.getLogger("dctc." + module).info(String.format(fmt, data));
        }
    }
    public static void warn(String module, String fmt, Object...data) {
        if (mode == Mode.STDERR) {
            System.err.println("dctc " + module + ": WARNING: " + String.format(fmt, data));
        } else if (mode == Mode.LOG4J) {
            Logger.getLogger("dctc." + module).warn(String.format(fmt, data));
        }
    }
    public static void error(String module, String fmt, Object...data) {
        if (mode == Mode.STDERR) {
            System.err.println("dctc " + module + ": ERROR: " + String.format(fmt, data));
        } else if (mode == Mode.LOG4J) {
            Logger.getLogger("dctc." + module).error(String.format(fmt, data));
        }
    }

    public static void warn(String module, String message, Throwable t) {
        if (mode == Mode.STDERR) {
            if (lvl == Level.DEBUG) {
                System.err.println("dctc " + module + ": WARNING: " + message);
                t.printStackTrace();
            } else {
                System.err.println("dctc " + module + ": WARNING: " + message);
                System.err.println("  For more information, rerun with -V");
            }
        } else if (mode == Mode.LOG4J) {
            Logger.getLogger("dctc." + module).warn(message, t);
        }
    }

    public static void error(String module, String message, Throwable t) {
        if (mode == Mode.STDERR) {
            if (lvl == Level.DEBUG) {
                System.err.println("dctc " + module + ": ERROR: " + message + ":");
                t.printStackTrace();
            } else {
                System.err.println("dctc " + module + ": ERROR: " + message);

                System.err.println("  For more information, rerun with -V");
            }
        } else if (mode == Mode.LOG4J) {
            Logger.getLogger("dctc." + module).error(message, t);
        }
    }
    private static Mode mode = Mode.STDERR;
    private static Level lvl = Level.ERROR;
}
