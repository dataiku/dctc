package com.dataiku.dctc.clo;

import java.util.ArrayList;
import java.util.List;

import com.dataiku.dctc.configuration.GlobalConf;
import com.dataiku.dip.utils.IndentedWriter;

public class Usage {
    public static void print(List<Option> opts) {
        List<String> prettyOpts = new ArrayList<String>();
        List<String> descriptions = new ArrayList<String>();

        StringBuilder sb = new StringBuilder();
        for (Option opt: opts) {
            sb.setLength(0);
            String shortOpts = opt.getShortOption() != null
                ? opt.getShortOption().getOpts() : "";
            for (int i = 0; i < shortOpts.length(); ++i) {
                sb.append('-');
                sb.append(shortOpts.charAt(i));
                sb.append(", ");
            }
            List<String> longOpts = opt.getLongOption() != null
                ? opt.getLongOption().getOpts() : new ArrayList<String>();
            for (String longOpt: longOpts) {
                sb.append("--");
                sb.append(longOpt);
                sb.append(", ");
            }
            sb.setLength(sb.length() - 2); // Delete the latest comma
                                           // space.

            // Print the argument name if existing.
            if (opt.hasOption() && opt.getArgName() != null) {
                sb.append("=");
                sb.append(opt.getArgName());
            }

            prettyOpts.add(sb.toString());
            descriptions.add(opt.getDescription());
        }

        IndentedWriter indent = new IndentedWriter();
        indent.setTermSize(Math.min(120, GlobalConf.getColNumber()));

        int maxLength = maxLength(prettyOpts);
        indent.setIndentSize(maxLength + 6);
        for (int i = 0; i < prettyOpts.size(); ++i) {
            String prettyOpt = prettyOpts.get(i);
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
        int res = Integer.MIN_VALUE;
        for (String l: ls) {
            res = Math.max(res, l.length());
        }
        return res;
    }
}

