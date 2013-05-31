package com.dataiku.dctc;

import org.apache.commons.lang.exception.ExceptionUtils;
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
            if (lvl == Level.DEBUG) {
                System.err.println("dctc " + module + ": WARNING: " + message);
                t.printStackTrace();
            } else {
                System.err.println("dctc " + module + ": WARNING: " + message + ": " + buildCompleteExceptionMessage(t));
                System.err.println("  For more information, rerun with -V");
            }
        } else if (mode == Mode.LOG4J) {
            Logger.getLogger("dctc." + module).warn(message, t);
        }
    }

    private static String buildCompleteExceptionMessage(Throwable t) {
        StringBuilder sb = new StringBuilder();
        while (true) {
            sb.append(t.getClass().getCanonicalName() + ": " + t.getMessage());
            if (t.getCause() != null) {
                sb.append(", caused by: ");
            } else {
                break;
            }
            t = t.getCause();
        }
        return sb.toString();
    }

    public static void error(String module, String message, Throwable t) {
        if (mode == Mode.STDERR) {
            if (lvl == Level.DEBUG) {
                System.err.println("dctc " + module + ": ERROR: " + message);
                t.printStackTrace();
            } else {
                System.err.println("dctc " + module + ": ERROR: " + message + ": " + buildCompleteExceptionMessage(t));
                System.err.println("  For more information, rerun with -V");
            }
        } else if (mode == Mode.LOG4J) {
            Logger.getLogger("dctc." + module).error(message, t);
        }
    }
    private static Mode mode = Mode.STDERR;
    private static Level lvl = Level.ERROR;
}
