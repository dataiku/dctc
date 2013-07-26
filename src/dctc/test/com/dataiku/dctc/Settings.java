package com.dataiku.dctc;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class Settings {
    public static void setErr() throws UnsupportedEncodingException {
        if (stErr == null) {
            stErr = System.err;
        }
        err = new ByteArrayOutputStream();
        PrintStream errPrint = new PrintStream(err, true, "UTF-8");
        System.setErr(errPrint);
    }
    public static void setOut() throws UnsupportedEncodingException {
        if (stErr == null) {
            stOut = System.out;
        }
        out = new ByteArrayOutputStream();
        PrintStream outPrint = new PrintStream(out, true, "UTF-8");
        System.setOut(outPrint);
    }
    public static String getErr() throws UnsupportedEncodingException {
        return err.toString("UTF-8");
    }
    public static String getOut() throws UnsupportedEncodingException {
        return out.toString("UTF-8");
    }

    public static void setOutputs() throws UnsupportedEncodingException {
        setErr();
        setOut();
    }

    public static void resetOutputs()  {
        System.setErr(stErr);
        System.setOut(stOut);
    }

    private static ByteArrayOutputStream err;
    private static ByteArrayOutputStream out;

    private static PrintStream stErr;
    private static PrintStream stOut;
}
