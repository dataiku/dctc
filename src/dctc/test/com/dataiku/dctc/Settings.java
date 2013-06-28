package com.dataiku.dctc;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class Settings {
    public static void setErr() throws UnsupportedEncodingException {
        err = new ByteArrayOutputStream();
        PrintStream errPrint = new PrintStream(err, true, "UTF-8");
        System.setErr(errPrint);
    }
    public static void setOut() throws UnsupportedEncodingException {
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

    private static ByteArrayOutputStream err;
    private static ByteArrayOutputStream out;

}
