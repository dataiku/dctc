package com.dataiku.dctc.clo;

import java.util.ArrayList;
import java.util.List;

public class Usage {
    public static void print(List<Option> opts, Printer printer) {
        printer.start();
        for (Option opt: opts) {
            printer.newOption();

            { // Options
                { // Short
                    String shortOpts = opt.getShortOption() != null
                        ? opt.getShortOption().getOpts() : "";
                    for (int i = 0; i < shortOpts.length(); ++i) {
                        printer.add(shortOpts.charAt(i));
                    }
                }

                { // Long
                    List<String> longOpts = opt.getLongOption() != null
                        ? opt.getLongOption().getOpts() : new ArrayList<String>();
                    for (String longOpt: longOpts) {
                        printer.add(longOpt);
                    }
                    printer.endOptionListing();
                }
            }

            { // Options
                if (opt.hasOption() && opt.getArgName() != null) {
                    printer.addParam(opt.getArgName());
                }
            }

            { // Description
                printer.endOption();
                printer.addDescription(opt.getDescription());
            }
        }
        printer.print();
    }
}
