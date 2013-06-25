package com.dataiku.dctc.dispatch;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import com.dataiku.dctc.AutoGZip;
import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dip.datalayer.ColumnFactory;
import com.dataiku.dip.datalayer.Row;
import com.dataiku.dip.input.Format;
import com.dataiku.dip.output.CSVOutputFormatter;
import com.dataiku.dip.output.LineOutputFormatter;
import com.dataiku.dip.output.OutputFormatter;

public class SplitStreamFactory {
    protected static class Output {
        Output(OutputStream outputStream, OutputFormatter outputFormatter) {
            this.outputStream = outputStream;
            this.outputFormatter = outputFormatter;
        }
        ColumnFactory cf;
        public OutputStream outputStream;
        public OutputFormatter outputFormatter;
    }

    public SplitStreamFactory(GeneralizedFile dir, String prefix,
            String suffix, SplitFunction fct, String selectedColumn, Format inputFormat,
            boolean compress) {
        this.dir = dir;
        this.prefix = prefix;
        this.suffix = suffix;
        this.fct = fct;
        this.selectedColumn = selectedColumn;
        this.inputFormat = inputFormat;
        this.compress = compress;
    }

    // Called under lock, no need to relock
    private Output get(ColumnFactory sourceCF, Row row) throws IOException {
        String splitIndex = fct.split(row, selectedColumn  == null ?  null : sourceCF.column(selectedColumn));

        Output out = outputStreams.get(splitIndex);
        if (out == null) {
            out = newStream(splitIndex);
            out.cf = sourceCF;
            outputStreams.put(splitIndex, out);
            out.outputFormatter.header(sourceCF, out.outputStream);
        }
        return out;
    }

    // Called when we see a new file
    protected Output newStream(String splitIndex) throws IOException {
        String fileName = prefix + splitIndex + suffix + (compress ? ".gz" : "");
        fileName.replaceAll("/", "`_");
        GeneralizedFile out = dir.createSubFile(fileName, dir.fileSeparator());
        out.mkpath();
        System.out.println("CREATE OUT " + out.getAbsoluteAddress());

        OutputFormatter formatter = null;
        if (inputFormat.getType().equals("csv")) {
            formatter = new CSVOutputFormatter(inputFormat.getCharParam("separator"), true, false);
        } else if (inputFormat.getType().equals("line")) {
            formatter = new LineOutputFormatter();
        }
        return new Output(AutoGZip.buildOutput(out), formatter);
    }

    // Called by the processor output.
    // Synchronized because we directly write in the output files
    synchronized void emitRow(ColumnFactory sourceCF, Row row) throws IOException {
        Output o = get(sourceCF, row);
        o.outputFormatter.format(row, sourceCF, o.outputStream);
    }

    public synchronized void close() throws IOException {
        for (Entry<String, Output> it: outputStreams.entrySet()) {
            Output o = it.getValue();
            o.outputFormatter.footer(o.cf, o.outputStream);
            IOUtils.closeQuietly(it.getValue().outputStream);
        }
    }

    protected GeneralizedFile dir;
    protected String prefix;
    protected String suffix;
    private Format inputFormat;
    private Map<String, Output> outputStreams = new HashMap<String, SplitStreamFactory.Output>();
    private SplitFunction fct;
    private String selectedColumn;
    private boolean compress;
}
