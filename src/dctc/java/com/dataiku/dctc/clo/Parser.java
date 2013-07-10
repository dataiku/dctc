package com.dataiku.dctc.clo;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    public enum ErrorType {
        INVALID_OPTION
        , NONE
        , TOO_FEW_ARGUMENT
    }

    public void parser(String[] args) {
        notOption = new ArrayList<String>();
        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            if (arg.isEmpty()) {
                notOption.add(arg);
            }
            else {
                int arglen = arg.length();
                if (arg.charAt(0) == '-') {
                    if (arglen == 1) {
                        notOption.add(arg); // "-"
                    }
                    else {
                        if (arg.charAt(1) == '-') {
                            if (arglen == 2) {
                                // End of the options
                                for (int j = i + 1; i < args.length; ++j) {
                                    notOption.add(args[j]);
                                }
                                break;
                            }
                            else {
                                // Long options
                                boolean skipNextArgument = parseLong(i, args);
                                if (hasError()) {
                                    return;
                                }
                                if (skipNextArgument) {
                                    ++i;
                                }
                                continue;
                            }
                        }
                        else {
                            // Short options
                            boolean skipNextArgument = parseShort(i, args);
                            if (hasError()) {
                                return;
                            }
                            if (skipNextArgument) {
                                ++i;
                            }
                            continue;
                        }
                    }
                }
                else {
                    notOption.add(arg);
                }
            }
        }
    }

    // Getters - Setters
    public List<Option> getOptions() {
        return options;
    }
    public void setOptions(List<Option> options) {
        this.options = options;
    }
    public Parser withOptions(List<Option> options) {
        setOptions(options);
        return this;
    }

    public boolean parseLong(int idx, String[] args) {
        assert args[idx].startsWith("--")
            : "args[idx].startsWith(\"--\")";
        assert args[idx].length() > 2
            : "args[idx].length() > 2";

        Option opt = getLong(args[idx].substring(2));
        if (opt != null) {
            opt.dec();
            if (opt.hasOption()) {
                if (idx == args.length) {
                    error = ErrorType.TOO_FEW_ARGUMENT;
                    optError = args[idx].substring(2);
                    return false;
                }
                else {
                    opt.setArg(args[idx + 1]);

                    return true;
                }
            }
        }
        else {
            error = ErrorType.INVALID_OPTION;
            optError = args[idx].substring(2);
        }

        return false;
    }
    public boolean parseShort(int idx, String[] args) {
        assert args[idx].startsWith("-")
            : "args[idx].startsWith(\"-\")";
        assert args[idx].length() > 1
            : "args[idx].length() > 1";

        String arg = args[idx];
        for (int i = 1; i < arg.length(); ++i) {
            char shortOption = arg.charAt(i);
            Option opt = getShort(shortOption);
            if (opt == null) {
                error = ErrorType.INVALID_OPTION;
                optError = new String(arg.substring(i, i + 1));
                return false;
            }
            else {
                opt.dec(); // First character is a minus.
                if (opt.hasOption()) {
                    if (i == arg.length()) {
                        if (idx == args.length) {
                            error = ErrorType.TOO_FEW_ARGUMENT;
                            optError = new String(arg.substring(i, i + 1));
                            return false;
                        }
                        opt.setArg(args[idx + 1]);
                        return false;
                    }
                    else {
                        opt.setArg(arg.substring(i + 1));
                        return false;
                    }
                }
            }
        }

        return false;
    }
    public Option getShort(char optName) {
        for (Option opt: options) {
            if (opt.getShortOption() != null && opt.getShortOption().has(optName)) {
                return opt;
            }
        }
        return null;
    }
    public Option getLong(String optName) {
        for (Option opt: options) {
            if (opt.getLongOption() != null && opt.getLongOption().has(optName)) {
                return opt;
            }
        }
        return null;
    }
    public boolean hasError() {
        return error != ErrorType.NONE;
    }
    public ErrorType getErrorType() {
        return error;
    }
    public String getOptionError() {
        return optError;
    }
    public String prettyPrintError() {
        switch (error) {
        case INVALID_OPTION:
            return "Unknown option: " + optError;
        case NONE:
            return "No error";
        case TOO_FEW_ARGUMENT:
            return "Too few argument for the option " + optError;
        default:
            throw new Error("Never reached");
        }
    }
    public List<String> getArgs() {
        return notOption;
    }
    public boolean hasOption(char optName) {
        Option opt = getShort(optName);
        return hasOption(opt);
    }
    public boolean hasOption(Option opt) {
        if (opt != null) {
            return opt.getCount() != 0;
        }
        return false;
    }
    public boolean hasOption(String optName) {
        Option opt = getLong(optName);
        return hasOption(opt);
    }
    public String getOptionValue(String optName) {
        Option opt = getLong(optName);
        return getOptionValue(opt);
    }
    public String getOptionValue(char optName) {
        Option opt = getShort(optName);
        return getOptionValue(opt);
    }
    public String getOptionValue(Option opt) {
        if (opt != null) {
            return opt.getArg();
        }
        System.err.println("debug: null");
        return null;
    }

    // Attributes
    private List<Option> options;
    private ErrorType error = ErrorType.NONE;
    private String optError;
    private List<String> notOption;
}
