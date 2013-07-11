package com.dataiku.dctc.clo;

import java.util.ArrayList;
import java.util.List;

import com.dataiku.dctc.configuration.GlobalConf;
import com.dataiku.dip.utils.IndentedWriter;

class SimplePrinter implements Printer {
    public void description() {
        System.out.println("DESCRIPTION");
    }
    public void start() {
        System.out.println();

        lines = new ArrayList<String>();
        descriptions = new ArrayList<String>();
    }
    public void newOption() {

        sb.setLength(0);
    }
    public void add(char s) {
        sb.append('-');
        sb.append(s);
        sb.append(", ");
    }
    public void add(String l) {
        sb.append("--");
        sb.append(l);
        sb.append(", ");
    }
    public void addParam(String p) {
        sb.append('=');
        sb.append(p);
    }
    public void addDescription(String descrip) {
        descriptions.add(descrip);
    }
    public void endOptionListing() {
        sb.setLength(sb.length() - 2);
        sb.setLength(Math.max(0, sb.length()));
    }
    public void endOption() {
        lines.add(sb.toString());
    }
    public void synopsis(String cmdname, String syn) {
        System.out.println("SYNOPSIS");
        System.out.print("    dctc " + cmdname + " ");
        System.out.println(syn);
        System.out.println();
    }
    public void name(String cmdname, String tagline) {
        System.out.println("NAME");
        System.out.println("    dctc " + cmdname + " - " + tagline);
        System.out.println();
    }

    public void print() {
        IndentedWriter indent = new IndentedWriter();
        indent.setTermSize(Math.min(120, GlobalConf.getColNumber() - 2));

        int maxLength = maxLength(lines);
        indent.setIndentSize(maxLength + 4);
        for (int i = 0; i < lines.size(); ++i) {
            String prettyOpt = lines.get(i);
            System.out.print("  ");
            System.out.print(prettyOpt);

            // local line indentation.
            indent.withStartIndex(prettyOpt.length() + 2)
                .setFirstLineIndentsize(maxLength - prettyOpt.length() + 2);
            indent.print(descriptions.get(i));
        }
    }
    // Privates
    private static int maxLength(List<String> ls) {
        int res = 0;
        for (String l: ls) {
            res = Math.max(res, l.length());
        }
        return res;
    }


    // Attributes
    StringBuilder sb = new StringBuilder();
    private List<String> lines;
    private List<String> descriptions;
}

