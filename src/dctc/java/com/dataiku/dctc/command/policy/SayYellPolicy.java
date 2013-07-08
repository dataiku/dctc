package com.dataiku.dctc.command.policy;

import java.io.PrintStream;

class SayYellPolicy implements YellPolicy {
    public void yell(String what, String error, Throwable exception) {
        out.println(error); // FIXME
    }

    // Getters - Setters
    public PrintStream getOut() {
        return out;
    }
    public void setOut(PrintStream out) {
        this.out = out;
    }
    public SayYellPolicy withOut(PrintStream out) {
        setOut(out);
        return this;
    }

    // Attributes
    private PrintStream out;
}
