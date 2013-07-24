package com.dataiku.dctc.clo;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    public boolean parser(String[] args, List<OptionAgregator> opts) {
        int position = 0;
        notOption = new ArrayList<String>();
        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            if (arg.isEmpty()) {
                continue;
            }
            else {
                if (arg.charAt(0) == '-' && arg.length() > 1) {
                    if (arg.length() == 2
                        && arg.charAt(1) == '-') {
                        // End of the option
                        for (int j = i + 1; j < args.length; ++j) {
                            notOption.add(args[j]);
                        }
                        return true;
                    }
                    else {
                        // We are in case of option
                        String optName = arg.substring(1);
                        while (true) {
                            if (optName.isEmpty()) {
                                break;
                            }
                            ++position;

                            boolean br = false;
                            for (OptionAgregator opt: opts) {
                                int k = 0;

                                if (opt.has(optName) == optName.length()
                                    && opt.hasArgument()) {
                                    // Need to put the next argument
                                    if (i + 1 < args.length) {
                                        optName += "=" + args[i + 1];
                                        ++i;
                                    }
                                    else {
                                        few(optName.substring(0, opt.has(optName)));
                                    }
                                }

                                k = opt.dec(optName, position);

                                if (k != 0) {
                                    optName = optName.substring(k);
                                    br = true;
                                    break;
                                }
                            }
                            if (!br) {
                                unknown(optName);
                                return false;
                            }
                        }

                    }
                }
                else {
                    notOption.add(arg);
                }
            }
        }
        return true;
    }

    // Getters - Setters
    public List<String> getNotOption() {
        return notOption;
    }
    public void setNotOption(List<String> notOption) {
        this.notOption = notOption;
    }
    public Parser withNotOption(List<String> notOption) {
        setNotOption(notOption);
        return this;
    }
    public List<String> getArgs() {
        return notOption;
    }
    public String prettyPrintError( ) {
        return error;
    }
    private void few(String optName) {
        error = "Missing operands for the option " + optName;
    }
    private void unknown(String optName) {
        if (optName.startsWith("-")) {
            error = "Unknown option: " + optName.substring(1);
        }
        else {
            error = "Unknown option: " + optName.substring(0, 1);
        }
    }

    // Attributes
    private List<String> notOption;
    private String error;
}
