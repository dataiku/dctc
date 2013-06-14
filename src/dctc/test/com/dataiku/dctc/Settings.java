package com.dataiku.dctc;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Settings {
    public static void setErr() {
        err = new ByteArrayOutputStream();
        PrintStream errPrint = new PrintStream(err);
        System.setErr(errPrint);
    }
    public static void setOut() {
        out = new ByteArrayOutputStream();
        PrintStream outPrint = new PrintStream(out);
        System.setOut(outPrint);
    }
    public static String getErr() {
        return err.toString();
    }
    public static String getOut() {
        return out.toString();
    }

    public static void setOutputs() {
        setErr();
        setOut();
    }

    private static ByteArrayOutputStream err;
    private static ByteArrayOutputStream out;

}
