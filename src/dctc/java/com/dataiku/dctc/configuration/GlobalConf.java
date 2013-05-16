package com.dataiku.dctc.configuration;

import java.util.Map;
import java.util.Map.Entry;

import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.display.DisplayFactory;
import com.dataiku.dctc.display.ThreadedDisplay;
import com.dataiku.dctc.exception.UserException;
import com.dataiku.dip.utils.DKUtils;


public class GlobalConf {
    public static String confFile() {
        return GlobalConstants.userHome + (GlobalConstants.isWindows ? "/dctcrc" : "/.dctcrc");
    }
    public static void setGlobalSettings(Map<String, String> settings) {
        for (Entry<String, String> elt: settings.entrySet()) {
            String key = elt.getKey();
            String value = elt.getValue();

            if (key.equals("display")) {
                display = new DisplayFactory(value);
            }
            else if (key.equals("thread_max")) {
                threadLimit = Integer.parseInt(value);
            }
            else if (key.equals("globbing")) {
                String lowerValue = value.toLowerCase();
                resolveGlobbing = getBoolean(value);
                if (resolveGlobbing == null) {
                    System.err.println("Unknown value (" + value + ") for globbing settings");
                }
            }
            else if (key.equals("show_git_hash")) {
                showGitHash = getBoolean(value);
                if (showGitHash == null) {
                    System.err.println("Unknown value (" + value + ") for show git hash settings");
                }
            }
            else {
                throw new UserException("In configuration file: invalid option '" + key + "' in [global] section");
            }
        }
    }
    public static boolean isValid() {
        return threadLimit > 0;
    }
    public static boolean printInvalidSettings() {
        if (threadLimit < 0) {
            System.err.println("Global settings check: the number of thread if negative or null (value: " + threadLimit + ").");
            return false;
        }
        return true;
    }

    public static void setDisplay(String displayName) {
        display = new DisplayFactory(displayName);
    }
    public static ThreadedDisplay getDisplay() {
        return display.build();
    }
    public static int getThreadLimit() {
        return threadLimit;
    }

    public synchronized static int getColNumber() {
        if (colNumber == 0) {
            if (GlobalConstants.isWindows) {
                colNumber = 80; // Unsupported
            } else {
                try {
                    String s = new String(DKUtils.execAndGetOutput(new String[]{"sh", "-c", "stty size < /dev/tty"}, null), "utf8");
                    colNumber =  Integer.parseInt(s.replace("\n", "").split(" ")[1]);
                } catch (Exception e) {
                    System.err.println("Can't compute terminal width");
                    e.printStackTrace();
                    colNumber =  80;
                }
            }
        }
        return colNumber;
    }
    public static boolean getResolveGlobbing() {
        return resolveGlobbing;
    }
    public static boolean isInteractif() {
        return System.console() != null;
    }
    public static String pathSeparator() {
        if (GlobalConstants.isWindows) {
            return ";";
        } else {
            return ":";
        }
    }
    public static boolean showGitHash() {
        return showGitHash;
    }

    // Privates
    private static Boolean getBoolean(String value) {
        String lowerValue = value.toLowerCase();
        if ("yes".startsWith(lowerValue) || "true".startsWith(lowerValue) || "1".equals(lowerValue)) {
            return true;
        }
        else if ("no".startsWith(lowerValue) || "false".startsWith(lowerValue) || "0".equals(lowerValue)) {
            return false;
        } else {
            return null;
        }
    }
    // Attributes
    static private int colNumber;
    static private DisplayFactory display = new DisplayFactory("auto");
    static private int threadLimit = Runtime.getRuntime().availableProcessors();
    static private Boolean resolveGlobbing = true;
    static private Boolean showGitHash = true;
}
