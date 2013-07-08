package com.dataiku.dctc.command.policy;

import java.io.PrintStream;

public class HowlPolicy implements YellPolicy {

    public void yell(String what, String error, Throwable exception) {
        if (exception != null) {
            out.println("dctc " + what + ": " + error);
            out.println("debug: stack trace: ");
            out.flush();
            exception.printStackTrace();
        }
        else {
            yell(what, error);
        }
    }
    public void yell(String what, String error) {
        out.println("dctc " + what + ": " + error);
        out.println("debug: No stack trace.");
    }

    // Getters-Setters
    public PrintStream getOut() {
        return out;
    }
    public void setOut(PrintStream out) {
        this.out = out;
    }
    public HowlPolicy withOut(PrintStream out) {
        setOut(out);
        return this;
    }

    // Attributes
    private PrintStream out;
}
