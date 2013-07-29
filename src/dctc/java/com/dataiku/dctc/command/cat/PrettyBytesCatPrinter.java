package com.dataiku.dctc.command.cat;

import static com.dataiku.dctc.utils.CharUtils.showNonPrintable;

import java.io.IOException;

class PrettyBytesCatPrinter extends AbstractCatPrinter {
    public void print(String line) {
        byte[] l = line.getBytes();

        try {
            getHeader().print(line);
            for (int i = 0; i < l.length; ++i) {
                if (prettyChar) {
                    showNonPrintable(System.out, l[i], showTabulation);
                }
                else {
                    showTabulation(l[i]);
                }
            }
            getEol().print();
        }
        catch (IOException e) {
            //yell("Unexpected error while reading " + file.givenName(), e, 2);
        }
    }
    public void end() {
    }

    // Getters - Setters
    public boolean getShowTabulation() {
        return showTabulation;
    }
    public void setShowTabulation(boolean showTabulation) {
        this.showTabulation = showTabulation;
    }
    public PrettyBytesCatPrinter withShowTabulation(boolean showTabulation) {
        setShowTabulation(showTabulation);
        return this;
    }
    public boolean getPrettyChar() {
        return prettyChar;
    }
    public void setPrettyChar(boolean prettyChar) {
        this.prettyChar = prettyChar;
    }
    public PrettyBytesCatPrinter withPrettyChar(boolean prettyChar) {
        setPrettyChar(prettyChar);
        return this;
    }

    private void showTabulation(byte x) {
        if (showTabulation && x == '\t') {
            System.out.print("^I");
        }
        else {
            System.out.print((char) x);
        }
    }

    // Attributes
    private boolean prettyChar;
    private boolean showTabulation;

}

