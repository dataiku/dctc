package com.dataiku.dctc.configuration;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import com.dataiku.dctc.DCTCLog;
import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.display.DisplayFactory;
import com.dataiku.dctc.display.ThreadedDisplay;
import com.dataiku.dctc.exception.UserException;
import com.dataiku.dip.utils.BooleanUtils;
import com.dataiku.dip.utils.IntegerUtils;
import com.dataiku.dip.utils.DKUtils;

public class GlobalConf {
    public static File confFile() {
        return new File(confPath());
    }
    public static File sshConfigFile() {
        return new File(sshConfigPath());
    }
    public static String confPath() {
        return GlobalConstants.userHome + (GlobalConstants.isWindows ? "/dctcrc" : "/.dctcrc");
    }
    public static String sshConfigPath() {
        return GlobalConstants.userHome + "/.ssh/config";
    }
    public static void setGlobalSettings(Map<String, String> settings) {
        for (Entry<String, String> elt: settings.entrySet()) {
            String key = elt.getKey();
            String value = elt.getValue();

            if (key.equals("display")) {
                display = value;
            }
            else if (key.equals("thread_max")) {
                threadLimit = IntegerUtils.toInt(value);
            }
            else if (key.equals("globbing")) {
                resolveGlobbing = BooleanUtils.toBoolean(value, false);
            }
            else {
                throw new UserException("dctc conf: Invalid option '" + key + "' in [global] section");
            }
        }
    }
    public static void setDisplay(String displayName) {
        display = displayName;
    }
    public static ThreadedDisplay getDisplay() {
        return new DisplayFactory(display).build();
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
                    System.err.println("dctc global conf: Can't compute terminal width (set it to 80).");
                    DCTCLog.warn("global conf", "Cannot compute terminal width", e);
                    colNumber = 80;
                }
            }
        }
        return colNumber;
    }
    public static boolean getResolveGlobbing() {
        return resolveGlobbing;
    }
    public static String pathSeparator() {
        if (GlobalConstants.isWindows) {
            return ";";
        }
        else {
            return ":";
        }
    }

    // Attributes
    static private int colNumber;
    static private String display = "auto";
    static private int threadLimit = Runtime.getRuntime().availableProcessors();
    static private Boolean resolveGlobbing = true;
}
