package com.dataiku.dctc.clo;

import java.util.List;

public class Usage {
    public static void print(List<OptionAgregator> opts, Printer printer) {
        printer.start();
        for (OptionAgregator opt: opts) {
            printer.newOption();

            { // Options
                for (Option o: opt.getOpts()) {
                    o.print(printer);
                }
                printer.endOptionListing();
            }

            { // Options
                if (opt.hasArgument()) {
                    printer.addParam(opt.getArgumentName());
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
