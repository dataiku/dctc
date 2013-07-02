package com.dataiku.dctc.command;

import static com.dataiku.dip.utils.PrettyString.scat;

import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.Options;
import org.apache.commons.lang.NotImplementedException;

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


public class Dispatch extends ListFilesCommand {
    public String tagline() {
        return "Dispatches the content of input files to output files.";
    }
    public void longDescription(IndentedWriter printer) {
        printer.paragraph(scat("This command split the input files to output files. Input files are"
                               ,"split in records based on the specified format."
                               ,"For each record, the target output file is computed according to"
                               ,"a dispatch function, and the record is appended to the computed"
                               ,"output.")
                          ,scat("For example this uses the value of the first column of each line"
                                ,"of each file in 'input/'. This value becomes the target file name"
                                ,"within the 'dispatched/' folders:"
                                ,"dctc dispatch inputs/ dispatched/ -function value -column col_0")
                          ,scat("Dispatch supports delimited files (CSV, TSV)."));
    }
    @Override
    protected Options setOptions() {
        Options options = new Options();
        options.addOption("c", "compress", false, "Compress all files (add a .gz extension).");

        longOpt(options, "Prefix the names of output files with a constant string", "prefix", "p", "pre");
        longOpt(options, "Suffix the names of output files with a constant string", "suffix", "s", "suf");
        longOpt(options, "Column to use for 'value', 'hash' and 'time' functions.", "column", "col", "column-name");
        longOpt(options,
                "Function to use to dispatch (one of 'random', 'value', 'hash', or 'time').",
                "function", "f", "fct-name");
        longOpt(options, "Number of output files to create for 'hash' and 'random' functions",
                "nb-files", "nf", "number");
        longOpt(options, "Time format string for the 'time' function.", "time-format", "tf", "format");
        longOpt(options, "Dispatch time period for the 'time' function"
                +" (one of 'year', 'month', 'day' or 'hour').", "time-period", "tp", "period");
        longOpt(options, "Format type for the input files. Only supports 'csv'.", "input-format", "if", "file-format");
        longOpt(options, "Separator character for CSV format", "input-separator", "isep", "separator");
        longOpt(options, "Quote character for CSV format", "input-quote", "iquot", "quote-character");
        return options;
    }

    @Override
    public final String cmdname() {
        return "dispatch";
    }
    /// Getters
    public String prefix() {
        String prefix = getOptionValue("p");
        if (prefix == null) prefix = "";
        return prefix;
    }
    public String postfix() {
        String postfix = getOptionValue("s");
        if (postfix == null) postfix = "";
        if (getOptionValue("if") != null && getOptionValue("if").equalsIgnoreCase("csv")) {
            postfix += ".csv";
        } else {
            postfix += ".txt";
        }
        return postfix;
    }
    public String splitFunction() {
        String splitFunction = getOptionValue("f");
        if (splitFunction == null) {
            splitFunction = "random";
        }
        return splitFunction;
    }
    public int fileNumber() {
        String str = getOptionValue("nf");
        if (str == null) return 8;
        return Integer.parseInt(str);
    }
    public String timeUnit() {
        return  getOptionValue("tp");
    }
    public String column() {
        return getOptionValue("col");
    }
    public String timeFormat() {
        return getOptionValue("tf");
    }

    @Override
    public final void execute(List<CopyTask> tasks) {
        if (tasks.size() == 0) return;

        SplitFunction fct = buildDispatchFunction();

        Format fmt = buildFormat(splitFunction(), tasks.get(0));
        // Test the format right away
        BasicFormatExtractorFactory.build(fmt);

        SplitFactory fact = new SplitFactory(dst, prefix(), postfix(), fct, column(), fmt, compress());
        ThreadedDisplay display = GlobalConf.getDisplay();
        CopyTasksExecutor exec = new CopyTasksExecutor(fact, display, GlobalConf.getThreadLimit());
        try {
            exec.run(tasks, false);
            fact.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
        try {
            if (!dst.exists()) {
                dst.mkpath();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
        String splitFunction = splitFunction();
        if (splitFunction.equals("random")) {
            if (fileNumber() == 0) {

            }
            return new RandomFunction(fileNumber());
        } else if (splitFunction.equals("value")) {
            if (column() == null) {
                throw new UserException("'column' is required for 'value' dispatch");
            }
            return new ValueFunction();
        } else if (splitFunction.equals("hash")) {
            if (column() == null) {
                throw new UserException("'column' is required for 'hash' dispatch");
            }
            return new HashFunction(fileNumber());
        } else if (splitFunction.equals("time")) {
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
        } else if (splitFunction.equals("merge")) {
            return new MergeFunction();
        } else {
            throw new UserException("Unknown dispatch function '" + splitFunction + "'.");
        }
    }
    private Format buildFormat(String function, CopyTask sampleTask) {
        String defaultFormat = (function.equalsIgnoreCase("random") ? "line" : "csv");
        String format = getOptionValue("if", defaultFormat);

        if (format.equalsIgnoreCase("auto")) {
            throw new NotImplementedException("Auto format is not yet implemented");
        } else if (format.equalsIgnoreCase("csv")) {
            String sep = getOptionValue("isep", ",");
            System.out.println("Use isep : ---" + sep + "---");
            return new Format("csv").withParam("separator", sep);
        } else if (format.equalsIgnoreCase("line")) {
            return new Format("line");
        } else {
            throw new NotImplementedException("Format '" + format + "' is not implemented, use 'csv' or 'line'");
        }
    }

    // Attributes
    private GeneralizedFile dst = null;
}
