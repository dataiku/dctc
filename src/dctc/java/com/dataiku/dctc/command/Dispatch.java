package com.dataiku.dctc.command;

import static com.dataiku.dip.utils.PrettyString.scat;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import com.dataiku.dctc.clo.OptionAgregator;
import com.dataiku.dctc.command.abs.ListFilesCommand;
import com.dataiku.dctc.configuration.GlobalConf;
import com.dataiku.dctc.copy.CopyTask;
import com.dataiku.dctc.copy.CopyTasksExecutor;
import com.dataiku.dctc.dispatch.DateFunction;
import com.dataiku.dctc.dispatch.HashFunction;
import com.dataiku.dctc.dispatch.MergeFunction;
import com.dataiku.dctc.dispatch.RandomFunction;
import com.dataiku.dctc.dispatch.SplitFactory;
import com.dataiku.dctc.dispatch.SplitFunction;
import com.dataiku.dctc.dispatch.ValueFunction;
import com.dataiku.dctc.display.ThreadedDisplay;
import com.dataiku.dctc.exception.UserException;
import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dip.input.Format;
import com.dataiku.dip.input.formats.BasicFormatExtractorFactory;
import com.dataiku.dip.partitioning.TimeDimension;
import com.dataiku.dip.utils.IndentedWriter;

public class Dispatch extends ListFilesCommand { // FIXME: Why?
    enum SplitFunctionNames {
        HASH
        , MERGE
        , RANDOM
        , TIME
        , VALUE
        ;
    }

    public String tagline() {
        return "Dispatches the content of input files to output files.";
    }
    public void longDescription(IndentedWriter printer) {
        printer.paragraph(scat("This command split the input files to output files. Input files are"
                               , "split in records based on the specified format."
                               , "For each record, the target output file is computed according to"
                               , "a dispatch function, and the record is appended to the computed"
                               , "output.")
                          , scat("For example this uses the value of the first column of each line"
                                , "of each file in 'input/'. This value becomes the target file name"
                                , "within the 'dispatched/' folders:"
                                , "dctc dispatch inputs/ dispatched/ -function value -column col_0")
                          , scat("Dispatch supports delimited files (CSV, TSV)."));
    }
    @Override
    protected void setOptions(List<OptionAgregator> opts) {
        opts.add(stdOption('c', "compress", "Compress all files (add a .gz extension)."));
        opts.add(stdOption('p', "prefix", "Prefix the names of output files with a constant string", true, "PREFIX"));
        opts.add(stdOption('s', "suffix", "Suffix the names of output files with a constant string", true, "SUFFIX"));
        opts.add(stdOption("l", "column", "Column to use for 'value', 'hash' and 'time' functions.", true, "COLUMN-NAME"));
        opts.add(stdOption('f', "function", "Function to use to dispatch (one of 'random', 'value', 'hash', or 'time').", true, "FUNCTION_NAME"));
        opts.add(stdOption("n", "nb-files", "Number of output files to create for 'hash' and 'random' functions", true, "NUMBER"));
        opts.add(stdOption("t", "time-format", "Time format string for the 'time' function.", true, "FORMAT"));
        opts.add(stdOption("r", "time-period", "Dispatch time period for the 'time' function (one of 'year', 'month', 'day' or 'hour').", true, "PERIOD"));
        opts.add(stdOption("o", "input-format", "Format type for the input files. Only supports 'csv'.", true, "FILE-FORMAT"));
        opts.add(stdOption("e", "input-separator", "Separator character for CSV format", true, "SEPARATOR"));
        opts.add(stdOption("q", "input-quote", "Quote character for CSV format", true, "QUOTE-CHAR"));
    }

    @Override
    public final String cmdname() {
        return "dispatch";
    }
    /// Getters
    public String prefix() {
        if (prefix == null) {
            prefix = getOptionValue("-prefix");

            if (prefix == null) {
                prefix = "";
            }
        }

        return prefix;
    }
    public String postfix() {
        if (postfix == null) {
            postfix = getOptionValue("-suffix");

            if (postfix == null) {
                postfix = "";
            }
            if (getOptionValue("-input-file") != null
                && getOptionValue("-input-file").equalsIgnoreCase("csv")) {
                postfix += ".csv";
            }
            else {
                postfix += ".txt";
            }
        }

        return postfix;
    }
    public SplitFunctionNames splitFunction() {
        if (splitFunction == null) {
            String fct = getOptionValue("-function").toLowerCase();
            for (SplitFunctionNames name: SplitFunctionNames.values()) {
                if (fct.equals(name.toString().toLowerCase())) {
                    splitFunction = name;
                    break;
                }
            }
            if (splitFunction == null) {
                error("Unknown function name: `" + fct + "'", 2);
                throw new EndOfCommand();
            }
        }

        return splitFunction;
    }
    public int fileNumber() {
        if (fileNumber == -1) {
            fileNumber = getIntOption("-nb-files", 8);
        }

        return fileNumber;
    }
    public String timeUnit() {
        if (timePeriod == null) {
            timePeriod = getOptionValue("-time-period");
        }

        return timePeriod;
    }
    public String column() {
        if (column == null) {
            column = getOptionValue("-column");
        }

        return column;
    }
    public String timeFormat() {
        if (timeFormat == null) {
            timeFormat = getOptionValue("-time-format");
        }

        return timeFormat;
    }

    @Override
    public final void execute(List<CopyTask> tasks) {
        if (tasks.isEmpty()) {
            error("No missing operands.", 2);
            return;
        }

        SplitFunction fct = buildDispatchFunction();

        Format fmt = buildFormat(splitFunction(), tasks.get(0));
        // Test the format right away
        BasicFormatExtractorFactory.build(fmt);

        SplitFactory fact = new SplitFactory(dst
                                             , prefix()
                                             , postfix()
                                             , fct
                                             , column()
                                             , fmt
                                             , compress());
        ThreadedDisplay display = GlobalConf.getDisplay();
        CopyTasksExecutor exec = new CopyTasksExecutor(fact
                                                       , display
                                                       , GlobalConf.getThreadLimit());
        try {
            exec.run(tasks, false);
            fact.close();
        }
        catch (IOException e) {
            error("Unexpected error while processing files", e, 2);
        }
    }
    @Override
    protected final boolean recursion() {
        return true;
    }
    @Override
    protected boolean includeLastPathElementInTarget() {
        return false;
    }
    @Override
    protected final boolean shouldAdd(GeneralizedFile src, GeneralizedFile dst, String root) {
        return true;
    }
    @Override
    protected void dstRoot(GeneralizedFile dst) {
        this.dst = dst;
    }
    @Override
    protected void leave(GeneralizedFile sourceDir) {
        // empty
    }
    @Override
    protected String proto() {
        return "[OPT...] InputPath... OutputPath";
    }
    @Override
    protected boolean deleteSource() {
        return false;
    }
    private SplitFunction buildDispatchFunction() {
        SplitFunctionNames splitFunction = splitFunction();
        if (splitFunction.equals("random")) {
            return new RandomFunction(fileNumber());
        }
        else if (splitFunction.equals("value")) {
            if (column() == null) {
                throw new UserException("'column' is required for 'value' dispatch");
            }

            return new ValueFunction();
        }
        else if (splitFunction.equals("hash")) {
            if (column() == null) {
                throw new UserException("'column' is required for 'hash' dispatch");
            }

            return new HashFunction(fileNumber());
        }
        else if (splitFunction.equals("time")) {
            if (column() == null) {
                throw new UserException("'column' is required for 'time' dispatch");
            }
            if (timeFormat() == null) {
                throw new UserException("timeFormat is required for 'time' dispatch");
            }
            if (timeUnit() == null) {
                throw new UserException("timePeriod is required for 'time' dispatch");
            }

            return new DateFunction(timeFormat(), TimeDimension.Period.valueOf(timeUnit().toUpperCase()));
        }
        else if (splitFunction.equals("merge")) {
            return new MergeFunction();
        }
        throw new UserException("Unknown dispatch function '"
                                + splitFunction + "'.");
    }
    private Format buildFormat(SplitFunctionNames function, CopyTask sampleTask) {
        String defaultFormat = function == SplitFunctionNames.RANDOM
            ? "line"
            : "csv";
        String format = getOptionValue("-input-file", defaultFormat);

        if (format.equalsIgnoreCase("auto")) {
            throw new NotImplementedException("Auto format is not yet implemented");
        }
        else if (format.equalsIgnoreCase("csv")) {
            String sep = getOptionValue("-input-separator", ",");

            return new Format("csv").withParam("separator", sep);
        }
        else if (format.equalsIgnoreCase("line")) {
            return new Format("line");
        }
        throw new NotImplementedException("Format '"
                                          + format
                                          + "' is not implemented, use 'csv' or 'line'");
    }

    // Attributes
    private GeneralizedFile dst = null;
    private String prefix;
    private String postfix;
    private int fileNumber;
    private SplitFunctionNames splitFunction;
    private String timePeriod;
    private String column;
    private String timeFormat;
}
