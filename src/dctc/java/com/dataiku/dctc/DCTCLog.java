package com.dataiku.dctc;

import org.apache.log4j.Logger;

public class DCTCLog {
    public enum Mode {
        STDERR,
        LOG4J
    };
    private static Mode mode = Mode.STDERR;

    public static void setMode(Mode newMode) {
        mode = newMode;
    }

    public static void info(String module, String message) {
        if (mode == Mode.STDERR) {
            System.err.println("dctc " + module + ": " + message);
        } else if (mode == Mode.LOG4J) {
            Logger.getLogger("dctc." + module).info(message);
        }
    }
    public static void warn(String module, String message) {
        if (mode == Mode.STDERR) {
            System.err.println("dctc " + module + ": WARNING: " + message);
        } else if (mode == Mode.LOG4J) {
            Logger.getLogger("dctc." + module).warn(message);
        }
    }
    public static void error(String module, String message) {
        if (mode == Mode.STDERR) {
            System.err.println("dctc " + module + ": ERROR: " + message);
        } else if (mode == Mode.LOG4J) {
            Logger.getLogger("dctc." + module).error(message);
        }
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
            System.err.println("dctc " + module + ": WARNING: " + message);
            t.printStackTrace();
        } else if (mode == Mode.LOG4J) {
            Logger.getLogger("dctc." + module).warn(message, t);
        }
    }
    public static void error(String module, String message, Throwable t) {
        if (mode == Mode.STDERR) {
            System.err.println("dctc " + module + ": ERROR: " + message);
            t.printStackTrace();
        } else if (mode == Mode.LOG4J) {
            Logger.getLogger("dctc." + module).error(message, t);
        }
    }
}
