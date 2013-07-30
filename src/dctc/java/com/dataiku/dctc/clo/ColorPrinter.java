package com.dataiku.dctc.clo;

import static com.dataiku.dctc.utils.Color.bold;
import static com.dataiku.dctc.utils.Color.green;
import static com.dataiku.dctc.utils.Color.red;

import java.util.ArrayList;
import java.util.List;

import com.dataiku.dctc.command.policy.YellPolicy;
import com.dataiku.dctc.configuration.GlobalConf;
import com.dataiku.dip.utils.IndentedWriter;

class ColorPrinter implements Printer {
    public void description() {
        System.out.println(bold(red("DESCRIPTION")));
    }
    public void start() {
        System.out.println();
        sizes = new ArrayList<Integer>();
        lines = new ArrayList<String>();
        descriptions = new ArrayList<String>();
    }
    public void newOption() {
        size = 0;
        sb.setLength(0);
    }
    public void add(char s) {
        sb.append(bold(red("-" + s)));
        sb.append(", ");
        size += 4;
    }
    public void add(String l) {
        sb.append(bold(red("-" + l)));
        sb.append(", ");
        size += 3 + l.length();
    }
    public void addParam(String p) {
        sb.append('=');
        sb.append(bold(green(p)));
        size += 1 + p.length();
    }
    public void addDescription(String descrip) {
        descriptions.add(descrip);
    }
    public void endOptionListing() {
        size -= 2;
        sb.setLength(sb.length() - 2);
        sb.setLength(Math.max(0, sb.length()));
    }
    public void endOption() {
        sizes.add(size);
        lines.add(sb.toString());
    }
    public void synopsis(String cmdname, String syn) {
        System.out.println(bold(red("SYNOPSIS")));
        System.out.print("    ");
        System.out.print(bold(red("dctc " + cmdname)));
        System.out.print(" ");
        System.out.println(syn.replaceAll("([a-zA-Z0-9]+)", green("$1")));
        System.out.println();
    }
    public void name(String cmdname, String tagline) {
        System.out.println(bold(red("NAME")));
        System.out.println("    dctc " + cmdname
                           + " - " + tagline);
        System.out.println();
    }

    public void print(YellPolicy yell) {
        IndentedWriter indent = new IndentedWriter();
        indent.setTermSize(Math.min(120, GlobalConf.getColNumber(yell) - 2));

        int maxLength = maxLength(sizes);
        indent.setIndentSize(maxLength + 4);
        for (int i = 0; i < lines.size(); ++i) {
            String prettyOpt = lines.get(i);
            System.out.print("  ");
            System.out.print(prettyOpt);

            // local line indentation.
            indent.withStartIndex(sizes.get(i) + 2)
                .setFirstLineIndentsize(maxLength
                                        - sizes.get(i)
                                        + 2);
            indent.print(descriptions.get(i));
        }
    }
    // Privates
    private static int maxLength(List<Integer> ls) {
        int res = 0;

        for (Integer l: ls) {
            res = Math.max(res, l);
        }

        return res;
    }

    // Attributes
    int size = 0;
    StringBuilder sb = new StringBuilder();
    private List<Integer> sizes;
    private List<String> lines;
    private List<String> descriptions;
}
