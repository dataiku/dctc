package com.dataiku.dctc.clo;

import java.util.List;

import com.dataiku.dctc.command.policy.YellPolicy;

public class Usage {
    public static void print(List<OptionAgregator> opts
                             , Printer printer
                             , YellPolicy yell) {
        printer.start();

        for (OptionAgregator opt: opts) {
            printer.newOption();

            { // Options
                for (Option o: opt.getOpts()) {
                    o.print(printer);
                }
                printer.endOptionListing();
            }

            { // Arguments
                if (opt.hasArgument()) {
                    printer.addParam(opt.getArgumentName());
                }
            }

            { // Description
                printer.endOption();
                printer.addDescription(opt.getDescription());
            }
        }

        printer.print(yell);
    }
}
