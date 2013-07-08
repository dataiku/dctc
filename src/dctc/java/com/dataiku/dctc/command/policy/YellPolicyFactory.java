package com.dataiku.dctc.command.policy;

import java.io.PrintStream;

public class YellPolicyFactory {
    public YellPolicyFactory() {
        if (System.getenv("DCTC_DEBUG") != null) {
            lvl = WarnLevel.DEBUG;
        }
        else if (System.getenv("DCTC_SILENT") != null) {
            lvl = WarnLevel.DEBUG;
        }
        else {
            lvl = WarnLevel.ERROR;
        }
    }

    public enum WarnLevel {
        SILENT
        , ERROR
        , DEBUG
    }
    public YellPolicy build() {
        switch(lvl) {
        case DEBUG:
            return new HowlPolicy()
                .withOut(out);
        case ERROR:
            return new SayYellPolicy()
                .withOut(out);
        case SILENT:
            return new QuashPolicy();
        default:
            throw new Error("Never reached.");
        }
    }

    // Getters-Setters
    public PrintStream getOut() {
        return out;
    }
    public void setOut(PrintStream out) {
        this.out = out;
    }
    public YellPolicyFactory withOut(PrintStream out) {
        setOut(out);
        return this;
    }
    public WarnLevel getLvl() {
        return lvl;
    }
    public void setLvl(WarnLevel lvl) {
        this.lvl = lvl;
    }
    public YellPolicyFactory withLvl(WarnLevel lvl) {
        setLvl(lvl);
        return this;
    }

    // Attributes
    private WarnLevel lvl;
    private PrintStream out = System.err;
}
