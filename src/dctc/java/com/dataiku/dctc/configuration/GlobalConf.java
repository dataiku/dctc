package com.dataiku.dctc.configuration;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.clo.PrinterFactory.PrinterType;
import com.dataiku.dctc.command.policy.YellPolicy;
import com.dataiku.dctc.display.DisplayFactory;
import com.dataiku.dctc.display.ThreadedDisplay;
import com.dataiku.dctc.exception.UserException;
import com.dataiku.dip.utils.BooleanUtils;
import com.dataiku.dip.utils.DKUtils;
import com.dataiku.dip.utils.IntegerUtils;

public class GlobalConf {
    public static File confFile() {
        return new File(confPath());
    }
    public static File sshConfigFile() {
        return new File(sshConfigPath());
    }
    public static String confPath() {
        return GlobalConstants.userHome
            + (GlobalConstants.isWindows
               ? "/dctcrc"
               : "/.dctcrc");
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
            else if (key.equals("usage")) {
                if (value.equals("simple")) {
                    setPrinterType(PrinterType.SIMPLE);
                }
                else if (value.equals("colored")) {
                    setPrinterType(PrinterType.COLORED);
                }
            }
            else {
                throw new UserException("dctc conf: Invalid option '"
                                        + key
                                        + "' in [global] section");
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

    public synchronized static int getColNumber(YellPolicy yell) {
        if (colNumber == 0) {
            if (GlobalConstants.isWindows) {
                colNumber = 80; // Unsupported
            }
            else {
                try {
                    String[] line = new String[]{"sh"
                                                 , "-c"
                                                 , "stty size < /dev/tty"};
                    String s
                        = new String(DKUtils.execAndGetOutput(line
                                                              , null)
                                     , "utf8");
                    colNumber =  Integer.parseInt(s
                                                  .replace("\n"
                                                           , "")
                                                  .split(" ")[1]);
                }
                catch (Exception e) {
                    yell.yell("conf"
                              , "Can't compute terminal width (set it"
                              + " to 80)."
                              , e);
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
    public static PrinterType getPrinterType() {
        return printerType;
    }
    public static void setPrinterType(PrinterType printerType_) {
        printerType = printerType_;
    }

    // Attributes
    static private PrinterType printerType = PrinterType.COLORED;
    static private int colNumber;
    static private String display = "auto";
    static private int threadLimit
        = Runtime.getRuntime().availableProcessors();
    static private Boolean resolveGlobbing = true;
}
